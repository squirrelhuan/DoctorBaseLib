package cn.demomaster.huan.doctorbaselibrary.view.activity.order;

import android.app.Activity;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderDoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.widget.time.DateTimePickerPopView;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.DisplayUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.loading.LoadingCircleView;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.CustomDialog;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDActionDialog;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDDialog;
import com.alibaba.fastjson.JSON;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单base
 *
 * @author squirrel桓
 * @date 2019/1/9.
 * description：
 */
public class BaseOrderListActivity extends BaseActivity {

    private String dateTime_ydm;
    private String dateTime_hour;
    private String dateTime_minute;

    /**
     * 时间选择器
     *
     * @param customDialog
     * @param contentView
     * @param requestId
     * @param sourceId
     * @param isFirstMotify
     * @param onActionResultListener
     */
    public void showDateTimePick(final CustomDialog customDialog, View contentView, final String requestId, final String sourceId, final boolean isFirstMotify, final OnActionResultListener onActionResultListener) {
        DateTimePickerPopView timePickerPopWin = new DateTimePickerPopView.Builder(mContext, contentView, new DateTimePickerPopView.OnTimePickListener() {
            @Override
            public void onTimePickCompleted(String ymd, String hour, String minute, String time) {
                //dateTime = hour + "-" + minute;
                dateTime_ydm = ymd;
                dateTime_hour = hour;
                dateTime_minute = minute;
                //UserHelper.getInstance().setDateTime(time);
                customDialog.dismiss();
                if (!isFirstMotify) {
                    changeAppointmentTime(requestId, time, onActionResultListener);
                } else {
                    proposeNewAppointTimeChange(requestId, time, sourceId, onActionResultListener);
                }
            }
        }).textConfirm("确定")
                .textCancel("取消")
                .btnTextSize(16)
                .viewTextSize(25)
                .setDefaultPosition(dateTime_ydm, dateTime_hour, dateTime_minute)
                .colorCancel(getResources().getColor(R.color.main_color))
                .colorConfirm(getResources().getColor(R.color.main_color))
                .colorSignText(getResources().getColor(R.color.main_color))
                .colorContentText(Color.GRAY, getResources().getColor(R.color.main_color), Color.GRAY)
                .setSignText(getResources().getString(R.string.year), getResources().getString(R.string.month), getResources().getString(R.string.day))
                .build();
    }


    /**
     * 第2次修改时间
     *
     * @param requestId
     * @param datatime
     * @param sourceId
     * @param onActionResultListener
     */
    public void proposeNewAppointTimeChange(String requestId, String datatime, String sourceId, final OnActionResultListener onActionResultListener) {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("trxId", requestId);
        map2.put("sourceId", sourceId);
        map2.put("newTime", datatime);
        map2.put("role", role);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);

        HttpUtils.proposeNewAppointTimeChange(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            onActionResultListener.onSuccess();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
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
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * 第一次修改时间
     *
     * @param requestId
     * @param datatime
     * @param onActionResultListener
     */
    public void changeAppointmentTime(String requestId, String datatime, final OnActionResultListener onActionResultListener) {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("trxId", requestId);
        map2.put("newTime", datatime);
        map2.put("role", role);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);

        HttpUtils.changeAppointmentTime(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            onActionResultListener.onSuccess();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
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
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    private QDActionDialog qdActionDialog;

    /**
     * 显示加载动画
     *
     * @param message
     */
    public void showLoading(String message) {
        if (qdActionDialog != null && qdActionDialog.isShowing()) {
            qdActionDialog.dismiss();
        }
        qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage(message)
                .create();
        qdActionDialog.show();
    }

    /**
     * 隐藏加载动画
     */
    public void hideLoading() {
        if (qdActionDialog != null) {
            qdActionDialog.dismiss();
        }
    }

    /**
     * 取消定单
     *
     * @param requestId
     * @param onActionResultListener
     */
    public void cancelTrx(String requestId, final OnActionResultListener onActionResultListener) {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("requestId", requestId);
        map2.put("role", role);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.cancelTrx(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            onActionResultListener.onSuccess();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
                        }
                        getData();
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        showLoading("取消定单");
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        hideLoading();
                        Log.i(TAG, "onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        hideLoading();
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    public void getData() {
    }

    /**
     * 医生端/客户端, 同意修改订单
     *
     * @param requestId
     * @param newTime
     * @param sourceId
     * @param onActionResultListener
     */
    public void approveAppointTimeChange(String requestId, String newTime, String sourceId, final OnActionResultListener onActionResultListener) {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("trxId", requestId);
        map2.put("newTime", newTime);
        map2.put("sourceId", sourceId);
        map2.put("role", role);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);

        HttpUtils.approveAppointTimeChange(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            onActionResultListener.onSuccess();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
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
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


    /**
     * 医生端/客户端, 拒绝修改订单
     *
     * @param requestId
     * @param sourceId
     * @param onActionResultListener
     */
    public void refuseAppointTimeChange(String requestId, String sourceId, final OnActionResultListener onActionResultListener) {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("trxId", requestId);
        map2.put("sourceId", sourceId);
        map2.put("role", role);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);

        HttpUtils.refuseAppointTimeChange(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            onActionResultListener.onSuccess();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
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
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * 提示用户是否调整新的时间
     *
     * @param requestId
     * @param sourceId
     * @param onActionResultListener
     */
    public void showRefuseAppointTimeChangeDialog(final String requestId, final String sourceId, final OnActionResultListener onActionResultListener) {
        new QDDialog.Builder(mContext).setTitle("")
                .setMessage("是否需要调整新的时间？")
                .setBackgroundRadius(50)
                .setText_color_foot(mContext.getResources().getColor(cn.demomaster.huan.doctorbaselibrary.R.color.main_color))
                .setGravity_body(Gravity.CENTER)
                .setMinHeight_body(DisplayUtil.dip2px(mContext, 100))
                .addAction("否", new QDDialog.OnClickActionListener() {//直接决绝
                    @Override
                    public void onClick(QDDialog dialog) {
                        refuseAppointTimeChange(requestId, sourceId, onActionResultListener);
                        dialog.dismiss();
                    }
                })
                .addAction("是", new QDDialog.OnClickActionListener() {//选择时间
                    @Override
                    public void onClick(QDDialog dialog) {
                        dialog.dismiss();
                        showSelectTimeDialog(requestId, sourceId, true, onActionResultListener);
                    }
                }).setGravity_header(Gravity.CENTER).setGravity_body(Gravity.CENTER).setGravity_foot(Gravity.CENTER).create().show();

    }

    /**
     * 取消订单提示
     *
     * @param requestId
     * @param type
     * @param onActionResultListener
     */
    public void showCancelDialog(final String requestId, int type, final OnActionResultListener onActionResultListener) {
        if (type == 0) {
            new QDDialog.Builder(mContext).setTitle("")
                    .setMessage("是否取消该订单？")
                    .setBackgroundRadius(50)
                    .setText_color_foot(mContext.getResources().getColor(cn.demomaster.huan.doctorbaselibrary.R.color.main_color))
                    .setGravity_body(Gravity.CENTER)
                    .setMinHeight_body(DisplayUtil.dip2px(mContext, 100))
                    .addAction("是", new QDDialog.OnClickActionListener() {//选择时间
                        @Override
                        public void onClick(QDDialog dialog) {
                            dialog.dismiss();
                            cancelTrx(requestId, new OnActionResultListener() {
                                @Override
                                public void onSuccess() {
                                    onActionResultListener.onSuccess();
                                }
                            });
                        }
                    })
                    .addAction("否", new QDDialog.OnClickActionListener() {//直接决绝
                        @Override
                        public void onClick(QDDialog dialog) {
                            dialog.dismiss();
                        }
                    }).setGravity_header(Gravity.CENTER).setGravity_body(Gravity.CENTER).setGravity_foot(Gravity.CENTER).create().show();
        } else {
            new QDDialog.Builder(mContext).setTitle("")
                    .setMessage("可在距约定时间的6小时前向接单专家发出更改服务时间和/或地址的请求;距离预约时间2小时外免费取消，2小时内取消将收取费用的70%作为违约费，请知悉！")
                    .setBackgroundRadius(50)
                    .setText_color_foot(mContext.getResources().getColor(cn.demomaster.huan.doctorbaselibrary.R.color.main_color))
                    .setGravity_body(Gravity.CENTER)
                    .setMinHeight_body(DisplayUtil.dip2px(mContext, 100))
                    .addAction("放弃取消", new QDDialog.OnClickActionListener() {//直接决绝
                        @Override
                        public void onClick(QDDialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .addAction("坚持取消", new QDDialog.OnClickActionListener() {//选择时间
                        @Override
                        public void onClick(QDDialog dialog) {
                            dialog.dismiss();
                            cancelTrx(requestId, new OnActionResultListener() {
                                @Override
                                public void onSuccess() {
                                    onActionResultListener.onSuccess();
                                }
                            });

                        }
                    }).setGravity_header(Gravity.CENTER).setGravity_body(Gravity.CENTER).setGravity_foot(Gravity.CENTER).create().show();
        }
    }

    /**
     * 修改时间弹窗
     *
     * @param requestId
     * @param sourceId
     * @param isFirstMotify
     * @param onActionResultListener
     */
    public void showSelectTimeDialog(String requestId, String sourceId, final boolean isFirstMotify, final OnActionResultListener onActionResultListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext, R.layout.item_pop_dialog_time);
        CustomDialog customDialog = builder.setCanTouch(true).create();
        View ccustomDialogView = customDialog.getContentView();
        showDateTimePick(customDialog, ccustomDialogView, requestId, sourceId, isFirstMotify, new OnActionResultListener() {
            @Override
            public void onSuccess() {
                onActionResultListener.onSuccess();
            }
        });
        customDialog.show();
    }

    /**
     * 保持原來的時間
     *
     * @param requestId
     * @param onActionResultListener
     */
    public void keepPreviousTime(String requestId, final OnActionResultListener onActionResultListener) {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("trxId", requestId);
        map2.put("role", role);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.keepPreviousTime(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            onActionResultListener.onSuccess();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
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
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    public static interface OnActionResultListener {
        void onSuccess();
    }

    public static interface OnActionClickListener {
        //接收订单
        void onAccept(String requestId);

        //直接拒绝订单
        // void onResufe(String requestId);
        //直接拒绝订单
        void onResufe(String requestId, String sourceId);

        void onCanel(String requestId);

        void onChart(String requestId, List<OrderDoctorModelApi.AdditionalInfo> additionalInfoList, boolean canChart);

        void onMotifyTime(String requestId);

        void onResufeAndMotifyTime(String requestId);

        /**
         * 同意修改订单
         *
         * @param requestId
         */
        void approveAppointTimeChange(String requestId, String newTime, String sourceId);

        /**
         * 医生端/客户端, 拒绝修改订单
         *
         * @param requestId
         */
        void refuseAppointTimeChange(String requestId, String sourceId);

        /**
         * @param requestId
         */
        void keepPreviousTime(String requestId);
    }
}
