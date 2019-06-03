package cn.demomaster.huan.doctorbaselib.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.util.AppStateUtil;
import cn.demomaster.huan.quickdeveloplibrary.base.BaseActivityParent;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.StatusBarUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.loader.LoadingDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

//implements BaseActivityInterface
public class BaseActivity extends BaseActivityParent {

    //状态栏模式
    public int StatusBarDarkMode = 0;
    public static String TAG ="CGQ";

    //导航栏
    //public ActionBarUtil.ActionBarInterface actionBarInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentBar();
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        StatusBarDarkMode = StatusBarUtil.StatusBarLightMode(this);
        //初始化数据
        //initData();
        //初始化视图
        //initViews();


        getActionBarLayout().setStateBarColorAuto(true);//状态栏文字颜色自动
        getActionBarLayout().setActionBarThemeColors(Color.BLACK,getResources().getColor(R.color.main_color));
    }

    /*@Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initViews() {
        //TODO 初始化视图
    }*/

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
        Log.i(TAG,"ClientPublickey length="+SessionHelper.getClientPublickey().getBytes().length);
        Log.d(TAG, str_map_param);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),str_map_param);
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

        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("登录中").setCanTouch(false).create();
        loadingDialog.show();


        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId",SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("token", rsaModel.getEncryptData());//p是病人端，d是医生端
        map2.put("uuid", rsaModel.getUuid());
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
                            PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            AppStateUtil.getInstance().setAppStateIsLogined(true);
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String token = jsonObject.get("token").toString();
                            String uuid = jsonObject.get("uuid").toString();
                            token = SecurityHelper.decryptData(token,uuid,SessionHelper.getClientPrivatekey());
                            SessionHelper.setToken(token);
                            SessionHelper.setUuid(uuid);
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "登录失败：" + response.getMessage());
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
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        loadingDialog.dismiss();
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
