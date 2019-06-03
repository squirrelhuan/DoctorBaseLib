package cn.demomaster.huan.doctorbaselibrary.view.activity.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.util.AppStateUtil;
import cn.demomaster.huan.doctorbaselibrary.view.widget.PopWinSingleDialog;
import cn.demomaster.huan.quickdeveloplibrary.base.BaseActivityParent;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.DisplayUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.StatusBarUtil;

import cn.jpush.android.api.JPushInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

//implements BaseActivityInterface
public class BaseActivity extends BaseActivityParent {

    //状态栏模式
    public int StatusBarDarkMode = 0;
    public static String TAG = "CGQ";

    @Override
    public int getHeadlayoutResID() {
        return R.layout.activity_actionbar_common_header;
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        if (getActionBarLayoutOld() != null) {
            getActionBarLayoutOld().getRightView().setVisibility(View.GONE);
        }
    }

    //导航栏
    //public ActionBarUtil.ActionBarInterface actionBarInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentBar();
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getMesageHelper().setColorStyle(getResources().getColor(R.color.white), getResources().getColor(R.color.main_color));
       /* getActionBarLayout().getActionBarTip().setLoadingStateListener(new ActionBarState.OnLoadingStateListener() {
            @Override
            public void onLoading(final ActionBarState.Loading loading) {
                //TODO 处理状态
                loading.setText("处理状态");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //loading.hide();
                loading.success("加载success");
            }
        });*/
    }

    //是否开启登陆态校验
    public boolean isVerifyLogin() {
        return true;
    }

    //验证登陆态结果
    public void setOnVerifyTokenResultListener(boolean isPass) {
        if (isPass) {
            if (getOnVerifyTokenResult() != null) {
                getOnVerifyTokenResult().onPass();
            }
        } else {
            if (getOnVerifyTokenResult() != null) {
                getOnVerifyTokenResult().onError();
                /*getActionBarLayout().getActionBarTip().showError("发生错误啦", new ActionBarTip.OnClickRetryListener() {
                    @Override
                    public void reTry() {
                        //PopToastUtil.ShowToast(mContext,"重试");
                        getOnVerifyTokenResult().onRetry();
                        //getActionBarLayout().getActionBarTip().hide();
                    }
                });*/
            }

            showGoToLogin();
        }
    }

    //验证通过继续执行
    public void onVerifyTokenSuccess() {

    }

    public OnVerifyTokenResultListener getOnVerifyTokenResult() {
        onVerifyTokenResultListener = new OnVerifyTokenResultListener() {
            @Override
            public void onPass() {
                onVerifyTokenSuccess();
            }

            @Override
            public void onError() {
               /* getActionBarLayout().getActionBarTip().showError("加载出错了" , new ActionBarTip.OnClickRetryListener() {
                    @Override
                    public void reTry() {
                        getOnVerifyTokenResult().onRetry();
                    }
                });*/
            }

            @Override
            public void onRetry() {
                verifyToken();
            }
        };
        return onVerifyTokenResultListener;
    }

    private OnVerifyTokenResultListener onVerifyTokenResultListener;

    public static interface OnVerifyTokenResultListener {
        void onPass();

        void onError();

        void onRetry();
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyToken();
    }

    /**
     * 判断校验态
     */
    public void verifyToken() {
        if (isVerifyLogin()) {//是否开启登陆态校验
            if (AppStateUtil.getInstance().isLogined()) {//已经登陆的状态检查登陆态是否可用
                verifyToken(new OnVerifyTokenListener() {
                    @Override
                    public void onSuccess() {
                        setOnVerifyTokenResultListener(true);
                    }

                    @Override
                    public void onFail(String error) {
                        setOnVerifyTokenResultListener(false);
                    }
                });
            } else {//不是登陆态，引导登陆
                setOnVerifyTokenResultListener(false);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        StatusBarDarkMode = StatusBarUtil.StatusBarLightMode(this);
        if (getActionBarLayoutOld() != null) {
            getActionBarLayoutOld().setActionBarThemeColors(Color.BLACK, getResources().getColor(R.color.main_color));
            getActionBarLayoutOld().setStateBarColorAuto(true);//状态栏文字颜色自动
            getActionBarLayoutOld().getRightView().setVisibility(View.GONE);
        }

    }


    //引导认证
    private void showGoToLogin() {
        /*AppStateUtil.getInstance().setAppStateIsLogined(false);
        popWinDialog_logout = PopWinSingleDialog.getInstance(mContext);
        popWinDialog_logout.setContentText("登陆状态失效，请重新登陆");
        popWinDialog_logout.setBtn_text("去登录");
        popWinDialog_logout.setOnClickListener_ok(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWinDialog_logout.dismiss();
                AppStateUtil.getInstance().logout(mContext);
                finish();
            }
        });
        popWinDialog_logout.show();*/

        AppStateUtil.getInstance().setAppStateIsLogined(false);
        new QDDialog.Builder(mContext)
                .setMessage("登陆状态失效，请重新登陆")
                .setBackgroundRadius(50)
                .setText_color_foot(mContext.getResources().getColor(R.color.main_color))
                .setGravity_body(Gravity.CENTER)
                .setMinHeight_body(DisplayUtil.dip2px(mContext, 100))
                .addAction("去登录", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) {
                        dialog.dismiss();
                        AppStateUtil.getInstance().logout(mContext);
                        finish();
                    }
                }).create().show();
    }

    //引导认证
    public void showGideAuth() {
        /*popWinDialog_Auth = PopWinSingleDialog.getInstance(mContext);
        popWinDialog_Auth.setContentText("为确保用户身份真实性，请先完成实人认证");
        popWinDialog_Auth.setBtn_text("开始");
        //, "为确保用户身份真实性，请先完成实人认证", , false);
        popWinDialog_Auth.setOnClickListener_ok(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWinDialog_Auth.dismiss();
                Class clazz = AppConfig.getInstance().getAuthorizationActivity();
                Intent intent = new Intent(mContext, clazz);
                startActivityForResult(intent, 111);
            }
        });
        popWinDialog_Auth.show();*/

        new QDDialog.Builder(mContext)
                .setMessage("为确保用户身份真实性，请先完成实人认证")
                .setBackgroundRadius(50)
                .setText_color_foot(mContext.getResources().getColor(R.color.main_color))
                .setGravity_body(Gravity.CENTER)
                .setMinHeight_body(DisplayUtil.dip2px(mContext, 100))
                .addAction("开始", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) {
                        dialog.dismiss();
                        Class clazz = AppConfig.getInstance().getAuthorizationActivity();
                        Intent intent = new Intent(mContext, clazz);
                        startActivityForResult(intent, 111);
                    }
                }).create().show();
    }

    public void exitApplication() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        /*final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定要退出登录吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        AppStateUtil.getInstance().logout(mContext);
                        finish();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        // normalDialog.
                    }
                });
        // 显示
        normalDialog.show();*/

        new QDDialog.Builder(mContext)
                .setMessage("是否确定退出当前账号？")
                .setWidth(DisplayUtil.dip2px(mContext, 260))
                .setBackgroundRadius(30)
                .setText_color_foot(mContext.getResources().getColor(R.color.main_color))
                .setGravity_body(Gravity.CENTER)
                .setText_color_body(mContext.getResources().getColor(R.color.main_color_gray_46))
                .setMinHeight_body(DisplayUtil.dip2px(mContext, 100))
                .addAction("取消", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .addAction("退出", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) {
                        AppStateUtil.getInstance().logout(mContext);
                        //去除别名
                        JPushInterface.deleteAlias(mContext,0);
                        finish();
                    }
                }).create().show();
    }

    //获取session
    public void createSession(final OnCreateSessionListener listener) {
        HttpUtils.createSession()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //成功获取公钥，保存到本地
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String publicKey = jsonObject.get("publicKey").toString();
                            String sessionId = jsonObject.get("sessionId").toString();
                            Log.i(TAG, "成功获取 publicKey: " + publicKey);
                            SessionHelper.setServerPublicKey(publicKey);
                            SessionHelper.setSessionId(sessionId);

                            //去服务端验证公钥可用
                            syncSession(new OnSyncSessionListener() {
                                @Override
                                public void onSuccess() {
                                    listener.onSuccess();
                                }

                                @Override
                                public void onFail(String error) {
                                    listener.onFail("fail:" + error);
                                }
                            });
                        } else {
                            listener.onFail("fail:" + response.getMessage());
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
                        listener.onFail("onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    //验证session
    public void syncSession(final OnSyncSessionListener listener) {
        Map map_param = new HashMap();
        map_param.put("sessionId", SessionHelper.getSessionId());
        map_param.put("clientPublicKey", SessionHelper.getClientPublickey());
        String str_map_param = JSON.toJSONString(map_param);
        Log.i(TAG, "ClientPublickey length=" + SessionHelper.getClientPublickey().getBytes().length);
        Log.d(TAG, str_map_param);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), str_map_param);
        HttpUtils.syncSession(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            listener.onSuccess();
                        } else {
                            listener.onFail("fail:" + response.getMessage());
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
                        listener.onFail("onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    //验证Token
    public void verifyToken(final OnVerifyTokenListener listener) {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        if (rsaModel == null) {
            if (listener != null) {
                listener.onFail("error");
            }
            return;
        }
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("role", role);//p是病人端，d是医生端
        map2.put("ios", null);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.verifyToken(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        } else {
                            PopToastUtil.ShowToast(mContext, "身份验证失败：" + response.getMessage());
                            if (listener != null) {
                                listener.onFail(response.getMessage());
                            }
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
                        if (listener != null) {
                            listener.onFail(throwable.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    public interface OnCreateSessionListener {
        void onSuccess();

        void onFail(String error);
    }

    public interface OnSyncSessionListener {
        void onSuccess();

        void onFail(String error);
    }

    public interface OnVerifyTokenListener {
        void onSuccess();

        void onFail(String error);
    }


}
