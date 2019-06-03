package cn.demomaster.huan.doctorbaselibrary.view.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.TimerSpan;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.net.URLConstant;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.util.DateTimeUtil;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.widget.textview.TimerButtonTextView;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.SoundHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.AnimationUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.DisplayUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.CustomDialog;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.Serializable;
import java.util.*;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;

/**
 * 开始计时页面
 */
public class OrderTimerActivity extends BaseActivity {

    TimerButtonTextView timeButton;
    TextView tvMinute;
    TextView tvSecond, tv_charge_tip;
    RelativeLayout rl_timer;
    private int activiType = 0;

    public static enum StateType implements Serializable {
        isRunning, isStart, isFinish, none
    }

    private StateType stateType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_timer);

        getActionBarLayoutOld().setTitle("计时");
        //Android 禁止界面截屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        timeButton = findViewById(R.id.time_button);
        tvMinute = findViewById(R.id.tv_minute);
        tvSecond = findViewById(R.id.tv_second);
        rl_timer = findViewById(R.id.rl_timer);
        tv_charge_tip = findViewById(R.id.tv_charge_tip);
        init();
    }

    //private boolean isRunning = false;
    private String trxId = null, requestName;
    TimerSpan timerSpan = null;
    Vibrator vibrator;

    //private String targetClassName;
    private void init() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动30毫秒
                vibrator.vibrate(50);
                SoundHelper.getInstance().playByResID(R.raw.timer_start);
                boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
                if (!isPatient) {
                    PopToastUtil.ShowToast(mContext, "只有病人可以开始和结束计时操作!");
                } else {
                    showStartOrStopDialog(trxId);
                }
            }
        });
        AnimationUtil.addScaleAnimition(timeButton, null);

        long last = SharedPreferencesHelper.getInstance().getLong(OrderTimeTmpTag, -1);
        mBundle = getIntent().getExtras();

        initData();
    }

    private void initData() {
        if (mBundle != null && mBundle.containsKey("activiType")) {
            activiType = mBundle.getInt("activiType");
            //targetClassName = mBundle.getString("targetClassName");
            if (activiType == 1) {//扫码进入
                stateType = StateType.none;
                if (mBundle.containsKey("doctorId")) {
                    String doctorId = mBundle.getString("doctorId", null);
                    //获取二维码数据
                    getDoctorInfoByQR(doctorId);
                }
            }
            if (activiType == 2) {//计时中自动跳转到此页面
                stateType = StateType.isRunning;
                timerSpan = (TimerSpan) mBundle.getSerializable("TimerSpan");

                if (mBundle.containsKey("trxId")) {
                    trxId = mBundle.getString("trxId", null);
                }
                if (mBundle.containsKey("requestName")) {
                    requestName = mBundle.getString("requestName", null);
                }
                getActionBarLayoutOld().setTitle("订单   "+requestName.replace("_", ""));
                //时间差
                long currentT = System.currentTimeMillis();
                long t = currentT - Integer.valueOf(timerSpan.getTimeSpan());
                SharedPreferencesHelper.getInstance().setLong(OrderTimeTmpTag, t);

                //handler.removeCallbacks(runnable);
            }
        }
        boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
        if (isPatient) {//用户端
            rl_timer.setVisibility(View.GONE);
            timeButton.setVisibility(View.VISIBLE);//"点击“开始”启动咨询计时，并作为计费依据。"
            tv_charge_tip.setText("点击“开始”启动咨询计时，并作为计费依据。");
        } else {
            rl_timer.setVisibility(View.VISIBLE);
            timeButton.setVisibility(View.GONE);
            //tv_charge_tip.setText("咨询中...");
            tv_charge_tip.setText("");
        }

        initState();

        //1.状态扫码进入，隐藏视图。等待网络接口返回数据后，再初始化状态

        //2.意外退出重新进入此页面，恢复计时状态

        //3.计时完成时处理
    }

    private int backgroundRadio = 30;

    /**
     * 开始或结束计时弹窗
     *
     * @param trxId
     */
    private void showStartOrStopDialog(final String trxId) {
        if (stateType == StateType.isStart) {//可以开始
            new QDDialog.Builder(mContext)
                    .setMessage("确定要开始计时吗？")
                    .setBackgroundRadius(50)
                    .setText_color_foot(mContext.getResources().getColor(cn.demomaster.huan.doctorbaselibrary.R.color.main_color))
                    .setGravity_body(Gravity.CENTER)
                    .setMinHeight_body(DisplayUtil.dip2px(mContext,100))
                    .addAction("确定", new QDDialog.OnClickActionListener() {
                        @Override
                        public void onClick(QDDialog dialog) {
                            beginTimeKeeping(trxId);
                            dialog.dismiss();
                        }
                    }).addAction("取消").create().show();
        } else if (stateType == StateType.isRunning) {//可以结束
            if (getTime() > 60) {
                new QDDialog.Builder(mContext)
                        .setMessage("确定要停止计时吗？")
                        .setBackgroundRadius(50)
                        .setText_color_foot(mContext.getResources().getColor(cn.demomaster.huan.doctorbaselibrary.R.color.main_color))
                        .setGravity_body(Gravity.CENTER)
                        .setMinHeight_body(DisplayUtil.dip2px(mContext,100))
                        .addAction("确定", new QDDialog.OnClickActionListener() {
                            @Override
                            public void onClick(QDDialog dialog) {
                                stopTimeKeeping(trxId);
                                dialog.dismiss();
                            }
                        }).addAction("取消").create().show();
            } else {
                PopToastUtil.ShowToast(mContext, "计时一分钟后可停止!");
            }
        }
    }

    private void initState() {
        boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
        switch (stateType) {
            case isStart:
                getActionBarLayoutOld().getLeftView().setVisibility(View.VISIBLE);
                timeButton.setText("开始");
                if (isPatient) {//用户端
                    rl_timer.setVisibility(View.GONE);
                    timeButton.setVisibility(View.VISIBLE);//"点击“开始”启动咨询计时，并作为计费依据。"
                    tv_charge_tip.setText("点击“开始”启动咨询计时，并作为计费依据。");
                } else {
                    rl_timer.setVisibility(View.VISIBLE);
                    timeButton.setVisibility(View.GONE);
                    //tv_charge_tip.setText("咨询中...");
                    tv_charge_tip.setText("");
                }
                break;
            case isRunning:
                getActionBarLayoutOld().getLeftView().setVisibility(View.GONE);
                rl_timer.setVisibility(View.VISIBLE);
                handler.postDelayed(runnable, 0);
                if (isPatient) {//用户端
                    timeButton.setText("结束");
                    tv_charge_tip.setText("点击“结束”停止计时，咨询结束。");
                } else {
                    //tv_charge_tip.setText("咨询中...");
                    tv_charge_tip.setText("");
                }
                break;
            case isFinish:
                getActionBarLayoutOld().getLeftView().setVisibility(View.GONE);
                rl_timer.setVisibility(View.VISIBLE);
                timeButton.setVisibility(View.GONE);
                SharedPreferencesHelper.getInstance().setLong(OrderTimeTmpTag, -1);
                handler.removeCallbacks(runnable);
                showFinishDialog();
                break;
            case none:
                getActionBarLayoutOld().getLeftView().setVisibility(View.VISIBLE);
                rl_timer.setVisibility(View.GONE);
                timeButton.setVisibility(View.GONE);
                tv_charge_tip.setText("");
                break;
        }
        //PopToastUtil.ShowToast(mContext, "已结束，共用时" + getTime() + "秒");
    }

    /**
     * 咨询结束弹窗
     */
    private void showFinishDialog() {
        final boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
        if (!isPatient) {
            return;
        }
       /* final Class clzz = getClassByClassName(targetClassName);
        clzz=EvaluateActivity.class;*/
        new QDDialog.Builder(mContext)
                .setMessage("咨询结束，是否马上前往支付？")
                .setBackgroundRadius(50)
                .setText_color_foot(mContext.getResources().getColor(cn.demomaster.huan.doctorbaselibrary.R.color.main_color))
                .setGravity_body(Gravity.CENTER)
                .setMinHeight_body(DisplayUtil.dip2px(mContext,100))
                .addAction("稍后操作", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) { //: "咨询结束，是否马上填写建议？
                        dialog.dismiss();
                        finish();
                    }
                }).addAction("去支付", new QDDialog.OnClickActionListener() {
            @Override
            public void onClick(QDDialog dialog) {
                Class orderPayActivity = AppConfig.getInstance().getOrderPayActivity();
                //Class adviceAndSuggestionsActivity = AppConfig.getInstance().getAdviceAndSuggestionsActivity();
                Class clzz = orderPayActivity;
                // Class clzz = isPatient ? orderPayActivity : adviceAndSuggestionsActivity;
                Intent intent = new Intent(mContext, clzz);
                Bundle bundle = new Bundle();
                bundle.putString("trxId", trxId);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                dialog.dismiss();
                finish();
            }
        }).setGravity_foot(Gravity.CENTER).create().show();
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshUI();
        }
    };
    private void refreshUI() {
        if (stateType == StateType.isRunning) {
            long t = getTime();
            long m = t / 60;
            long s = t % 60;
            tvMinute.setText(String.format("%0" + ((String.valueOf(m).length() == 1) ? 2 : (String.valueOf(m).length())) + "d", m));
            tvSecond.setText(String.format("%0" + 2 + "d", s));
            handler.postDelayed(runnable, 0);
        } else {
            SharedPreferencesHelper.getInstance().setLong(OrderTimeTmpTag, -1);
            handler.removeCallbacks(runnable);
        }
    }

    public static String OrderTimeTmpTag = "Order_Time_Tmp_Tag";
    public long getTime() {
        long now = System.currentTimeMillis();
        long last = SharedPreferencesHelper.getInstance().getLong(OrderTimeTmpTag, -1);
        if (last == -1) {
            last = now;
            SharedPreferencesHelper.getInstance().setLong(OrderTimeTmpTag, now);
        }
        long diff = getSecond(now - last);
        return diff;
    }

    //时间戳转字符串
    public static long getSecond(long diff) {
        //以秒为单位
        Long second = (diff / 1000);
        return second;
    }

    /**
     * 开始计时
     */
    private void beginTimeKeeping(final String trxId) {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        map2.put("requestId", trxId);
        map2.put("startTime", DateTimeUtil.getToday().getDateTimeStr());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, URLConstant.URL_BASE);
        retrofitInterface.beginTimeKeeping(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            stateType = StateType.isRunning;
                            initState();
                        } else {
                            //showMessage(response.getMessage());
                            PopToastUtil.ShowToast((Activity) mContext, "" + response.getMessage());
                        }
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                        //loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        //loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 结束计时
     *
     * @param trxId
     */
    private void stopTimeKeeping(final String trxId) {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        map2.put("requestId", trxId);
        map2.put("endTime", DateTimeUtil.getToday().getDateTimeStr());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, URLConstant.URL_BASE);
        retrofitInterface.stopTimeKeeping(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            stateType = StateType.isFinish;
                            initState();
                        } else {
                            //showMessage(response.getMessage());
                            PopToastUtil.ShowToast((Activity) mContext, "" + response.getMessage());
                        }
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                        //loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        //loadingDialog.dismiss();
                    }
                });
    }

    //根据二维码查找订单
    private void getDoctorInfoByQR(final String doctorId) {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        map2.put("d", doctorId);

        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, URLConstant.URL_BASE);
        retrofitInterface.getDoctorInfoByQR(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            Retsult retsult = JSON.parseObject(response.getData().toString(), Retsult.class);
                            showImage(retsult);
                        } else {
                            //showMessage(response.getMessage());
                            PopToastUtil.ShowToast((Activity) mContext, "" + response.getMessage());
                        }
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                        //loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        //loadingDialog.dismiss();
                    }
                });

    }

    private static CustomDialog customDialog;
    private static CustomDialog.Builder builder;

    /**
     * 显示医生资质照片
     *
     * @param retsult
     */
    private void showImage(Retsult retsult) {
        if (retsult != null && retsult.getTrxInfoList() != null && retsult.getTrxInfoList().size() > 0) {
            trxId = retsult.getTrxInfoList().get(0).getTrxId() + "";
           // getActionBarLayoutOld().setTitle(retsult.getTrxInfoList().get(0).getTrxName().replace("_", "  "));
            getActionBarLayoutOld().setTitle("订单   "+retsult.getTrxInfoList().get(0).getTrxName().replace("_", ""));
            stateType = StateType.isStart;
        }
        builder = new CustomDialog.Builder(mContext, R.layout.item_pop_dialog_doctor_info);
        customDialog = builder.setCanTouch(false).create();
        View customDialogView = customDialog.getContentView();
        ImageView imageView = customDialogView.findViewById(R.id.iv_picture);
        RequestOptions mRequestOptions = RequestOptions.centerCropTransform()
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                .error(R.mipmap.ic_launcher)
                .skipMemoryCache(false);//不做内存缓存
        TextView tv_time = customDialogView.findViewById(R.id.tv_time);
        Glide.with(mContext).load(retsult.getQualificationCerPhotoUrl()).apply(mRequestOptions).into(imageView);
        customDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);

        // 在底部，宽度撑满
        WindowManager.LayoutParams params = customDialog.getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

       /* int screenWidth = QMUIDisplayHelper.getScreenWidth(getContext());
        int screenHeight = QMUIDisplayHelper.getScreenHeight(getContext());
        params.width = screenWidth < screenHeight ? screenWidth : screenHeight;*/

        //getWindow().setWindowAnimations(R.style.FadeInPopWin);  //添加动画
        customDialog.getWindow().setWindowAnimations(-1);  //添加动画

        customDialog.getWindow().setAttributes(params);
        customDialog.getWindow().setGravity(Gravity.CENTER);

        customDialog.show();

        refreshTimer(tv_time);
       /* Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        }, 6000);*/

        //qdActionDialog = new QDActionDialog.Builder(mContext).setBackgroundColor(mContext.getResources().getColor(R.color.transparent_dark_cc)).setBackgroundRadius(50).setTopImage(R.mipmap.ic_launcher_doctor).create();
    }

    private int time = 5;//5秒后隐藏

    private void refreshTimer(final TextView tv_time) {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (time > 0) {
                            tv_time.setText(time + "");
                            time = time - 1;
                        } else {
                            time = 5;
                            hide();
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    public void hide() {
        if (customDialog != null) {
            customDialog.dismiss();
        }
        stateType = StateType.isStart;
        initState();
    }

    public static class Retsult {
        /**
         * qualificationCerPhotoUrl : http://drvisit-photo.oss-cn-hangzhou.aliyuncs.com/drvisit-photo-%E7%8E%8B%E8%8C%B9-1546846091030
         * trxInfoList : [{"trxId":53,"trxName":"全科_20190130161925"}]
         */

        private String qualificationCerPhotoUrl;
        private List<TrxInfoListBean> trxInfoList;

        public String getQualificationCerPhotoUrl() {
            return qualificationCerPhotoUrl;
        }

        public void setQualificationCerPhotoUrl(String qualificationCerPhotoUrl) {
            this.qualificationCerPhotoUrl = qualificationCerPhotoUrl;
        }

        public List<TrxInfoListBean> getTrxInfoList() {
            return trxInfoList;
        }

        public void setTrxInfoList(List<TrxInfoListBean> trxInfoList) {
            this.trxInfoList = trxInfoList;
        }

        public static class TrxInfoListBean {
            /**
             * trxId : 53
             * trxName : 全科_20190130161925
             */
            private int trxId;
            private String trxName;

            public int getTrxId() {
                return trxId;
            }

            public void setTrxId(int trxId) {
                this.trxId = trxId;
            }

            public String getTrxName() {
                return trxName;
            }

            public void setTrxName(String trxName) {
                this.trxName = trxName;
            }
        }

    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (stateType == StateType.isRunning) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                getOrderState();
                /*if (System.currentTimeMillis() - firstTime > 2000) {
                    PopToastUtil.ShowToast(mContext, "计时中请勿其他操作");
                    firstTime = System.currentTimeMillis();
                } else {
                    //finish();
                    //System.exit(0);
                }*/
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取当前有没有正在计时的订单
     */
    public void getOrderState() {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);

        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        retrofitInterface.getTrxInDiagnosing(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            try {
                                JSONArray jsonArray = JSON.parseArray(response.getData().toString());
                                if (jsonArray != null && jsonArray.size() > 0) {//有订单状态进行中跳转到计时页
                                    String mtrxId = JSON.parseObject(jsonArray.get(0).toString()).get("trxId").toString();
                                    String requestName = JSON.parseObject(jsonArray.get(0).toString()).get("requestName").toString();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("trxId", trxId);
                                    if(trxId.equals(mtrxId)){
                                        PopToastUtil.ShowToast(mContext, "计时中请勿其他操作");
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "fail：" + response.getMessage());
                        }
                        SharedPreferencesHelper.getInstance().setLong(OrderTimeTmpTag, -1);
                        Toast.makeText(mContext,"计时已结束",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

}
