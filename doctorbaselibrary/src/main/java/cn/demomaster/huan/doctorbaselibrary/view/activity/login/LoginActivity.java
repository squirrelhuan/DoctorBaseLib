package cn.demomaster.huan.doctorbaselibrary.view.activity.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.util.AppStateUtil;
import cn.demomaster.huan.doctorbaselibrary.util.PermissionManager;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.ActionBarInterface;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.QDLogger;
import cn.demomaster.huan.quickdeveloplibrary.util.StringVerifyUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.loader.LoadingDialog;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static cn.demomaster.huan.doctorbaselibrary.util.PermissionManager.SYSTEM_ALERT_WINDOW_CODE;


public class LoginActivity extends BaseActivity implements View.OnClickListener {


    EditText etTelephone;
    EditText etPassword;
    TextView btnLogin;
    TextView tvRegister;
    TextView tvFindPassword;
    RelativeLayout relRoot;
    private String TAG = "CGQ";
    @Override
    protected void onResume() {
        super.onResume();
        PermissionManager.getInstance(this).initPermission();
    }

    @Override
    public boolean isVerifyLogin() {
        return false; //false不检查登陆态
    }
    //根据需求定义自己需要关闭页面的action
    public static final String RECEIVER_ACTION_FINISH_LOGIN = "RECEIVER_ACTION_FINISH_LOGIN";

    private class FinishActivityRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //根据需求添加自己需要关闭页面的action
            if (RECEIVER_ACTION_FINISH_LOGIN.equals(intent.getAction())) {
                finish();
            }
        }
    }
    private FinishActivityRecevier mRecevier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBarLayoutOld().setActionBarModel(ActionBarInterface.ACTIONBAR_TYPE.NO_ACTION_BAR);

        etTelephone = findViewById(R.id.et_telephone);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        tvRegister = findViewById(R.id.tv_register);
        tvRegister.setOnClickListener(this);
        tvFindPassword = findViewById(R.id.tv_findPassword);
        tvFindPassword.setOnClickListener(this);
        relRoot = findViewById(R.id.rel_root);

        initViews();
        mRecevier = new FinishActivityRecevier();
        registerFinishReciver();
    }
    private void registerFinishReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_ACTION_FINISH_LOGIN);
        registerReceiver(mRecevier, intentFilter);
    }

    @Override
    protected void onDestroy() {
        if (mRecevier != null) {
            unregisterReceiver(mRecevier);
        }
        super.onDestroy();
    }

    public void initViews() {
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvFindPassword.setOnClickListener(this);
    }

    public void login(String telephone, String password) {

        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("登录中").setCanTouch(false).create();
        if(!((Activity) this).isFinishing()) {
            loadingDialog.show();
        }
        String role = AppConfig.getInstance().isPatient()? "P":"D";//病人 P   医生D
        Map map = new HashMap();
        map.put("phoneNumber", telephone);
        map.put("password", password);
        String map_str = JSON.toJSONString(map);
        Log.d(TAG, map_str);
        String str = SecurityHelper.jsSign(map_str, false);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("role", role);//p是病人端，d是医生端
        map2.put("SID", str);
        map2.put("ios", null);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.getLogin(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            AppStateUtil.getInstance().setAppStateIsLogined(true);
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String token = jsonObject.get("token").toString();
                            String uuid = jsonObject.get("uuid").toString();
                            String userName = jsonObject.get("userName").toString();
                            String alias  = jsonObject.get("alias").toString();
                            SharedPreferencesHelper.getInstance().putString("alias", alias);
                            JPushInterface.setAlias(mContext,0,alias);
                            token = SecurityHelper.decryptData(token, uuid, SessionHelper.getClientPrivatekey());
                            SessionHelper.setToken(token);
                            SessionHelper.setUserName(userName);
                            SessionHelper.setUuid(uuid);
                            SharedPreferencesHelper.getInstance().putBoolean("isFirstLogin", true);
                            startActivity(AppConfig.getInstance().getMainActivityActivity());

                            LoginActivity.this.finish();
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
                        PopToastUtil.ShowToast((Activity) mContext, "登录出错了");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        loadingDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_login) {
            if (tryToLogin()) {
                createSession(new OnCreateSessionListener() {
                    @Override
                    public void onSuccess() {
                        login(telephone, password);
                    }

                    @Override
                    public void onFail(String error) {

                    }
                });
            }
        } else if (id == R.id.tv_register) {
            startActivity(RegisterActivity.class);
        } else if (id == R.id.tv_findPassword) {
            startActivity(ResetPasswordActivity.class);
        }

    }

    String telephone;
    String password;

    private boolean tryToLogin() {
        telephone = etTelephone.getText().toString();
        password = etPassword.getText().toString();
        etTelephone.setError(null);
        etPassword.setError(null);
        boolean v1 = checkTelePhone(telephone);
        if (!v1) {
            return false;
        }
        boolean v2 = checkPassWord(password);
        return v2;
    }

    public boolean checkTelePhone(String username) {
        if (!StringVerifyUtil.validateMobilePhone(username)) {
            String str = getResources().getString(R.string.Cell_phone_format_error);
            etTelephone.setError(str);
            PopToastUtil.ShowToast((Activity) mContext, str);
            return false;
        }
        return true;
    }

    public boolean checkPassWord(String password) {
        if (!StringVerifyUtil.isPasswordValid(password)) {
            String str = getResources().getString(R.string.Incorrect_password_format);
            etPassword.setError(str);
            PopToastUtil.ShowToast((Activity) mContext, str);
            return false;
        }
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SYSTEM_ALERT_WINDOW_CODE == requestCode) {
            PermissionManager.getInstance(this).showProcess();
        } else {
            PermissionManager.getInstance(this).initPermission();
        }
    }

    //处理权限申请回调(写在Activity中)
    @Override
    public void onRequestPermissionsResult(int requestCode, @io.reactivex.annotations.NonNull String[] permissions, @io.reactivex.annotations.NonNull int[] grantResults) {
        PermissionManager.getInstance(this).initPermission();
    }

}
