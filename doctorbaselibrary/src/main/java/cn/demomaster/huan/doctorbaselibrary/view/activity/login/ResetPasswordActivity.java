package cn.demomaster.huan.doctorbaselibrary.view.activity.login;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.quickdeveloplibrary.helper.SmsCodeHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.StringVerifyUtil;
import com.alibaba.fastjson.JSON;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {


    EditText etTelephone;
    EditText etSmscode;
    TextView tvGetSmscode;
    EditText etPassword;
    EditText etRepassword;
    TextView tvReset;
    private String TAG = "CGQ";

    @Override
    public boolean isVerifyLogin() {
        return false; //false不检查登陆态
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getActionBarLayoutOld().setTitle(getResources().getString(R.string.registered_account));
        initViews();
    }

    public void initViews() {
        etTelephone = findViewById(R.id.et_telephone);
        etSmscode = findViewById(R.id.et_smscode);
        tvGetSmscode = findViewById(R.id.tv_get_smscode);
        etPassword = findViewById(R.id.et_password);
        etRepassword = findViewById(R.id.et_repassword);
        tvReset = findViewById(R.id.tv_reset);

        tvGetSmscode.setOnClickListener(this);
        tvReset.setOnClickListener(this);
        initSmsCodeHelper();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id ==R.id.tv_reset){
            register();
        }
    }

    private String smsCode;
    private String phone;
    private String password;

    SmsCodeHelper smsCodeHelper;

    //短信验证码处理
    private void initSmsCodeHelper() {
        phone = etTelephone.getText().toString();
        SmsCodeHelper.Builder builder = new SmsCodeHelper.Builder(phone, tvGetSmscode, new SmsCodeHelper.OnSmsCodeListener() {
            @Override
            public void onTimeChange(long time) {
                tvGetSmscode.setText(time + "s");
            }

            @Override
            public boolean onNextHttpGet() {
                phone = etTelephone.getText().toString();
                Boolean b = StringVerifyUtil.validateMobilePhone(phone);
                if (b) {
                    getSmsCode();
                } else {
                    PopToastUtil.ShowToast((Activity) mContext, "请填写正确的手机号");
                }
                return b;
            }

            @Override
            public void onReceiveSuccess(String tip) {
                Toast.makeText(mContext, "success：" + tip, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onReceiveFailure(String error) {
                Toast.makeText(mContext, "" + error, Toast.LENGTH_LONG).show();
            }
        });
        smsCodeHelper = builder.create();
        smsCodeHelper.start();

    }

    //发起网络请求获取验证码
    public void getSmsCode() {
        String telephone = etTelephone.getText().toString();
        HttpUtils.getSmsCode(telephone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "登录失败：" + response.getMessage());
                        }
                        smsCodeHelper.onReceiveSmsCodeSuccess(response.getMessage());
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                        smsCodeHelper.onReceiveSmsCodeFailure("onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    public void register() {

        smsCode = etSmscode.getText().toString();
        phone = etTelephone.getText().toString();
        password = etPassword.getText().toString();

        TextView[] views1 = {etTelephone, etSmscode, etPassword, etRepassword};
        for (TextView editView : views1) {
            if (editView.getText() == null || editView.getText().toString() == null || TextUtils.isEmpty(editView.getText().toString())) {
                String desc = "";
                if (editView.getContentDescription() != null) {
                    desc = editView.getContentDescription().toString();
                }
                editView.setError("不能为空");
                PopToastUtil.ShowToast((Activity) mContext, desc + "不能为空");
                return;
            }
        }

        if (!StringVerifyUtil.validateMobilePhone(etTelephone.getText().toString())) {
            etTelephone.setError("请填写正确的手机号");
            return;
        }
        if (!StringVerifyUtil.validatePassword(etPassword.getText().toString())) {
            etPassword.setError("密码格式不正确");
            return;
        }
        if (!StringVerifyUtil.validatePassword(etRepassword.getText().toString())) {
            etRepassword.setError("确认密码格式不正确");
            return;
        }
        if (!StringVerifyUtil.validatePassword(etRepassword.getText().toString())) {
            etRepassword.setError("确认密码格式不正确");
            return;
        }
        if (!etPassword.getText().toString().equals(etRepassword.getText().toString())) {
            PopToastUtil.ShowToast((Activity) mContext, "两次密码输入不一致");
            return;
        }

        createSession(new OnCreateSessionListener() {
            @Override
            public void onSuccess() {
                modifyPwd();
            }

            @Override
            public void onFail(String error) {
                PopToastUtil.ShowToast((Activity) mContext, "失败：" + error);
            }
        });
    }

    //验证短信验证码是否有效
    public void authSms(){

    }

    //发起网络请求获取验证码
    public void modifyPwd() {
        String role = AppConfig.getInstance().isPatient()? "P":"D";//病人 P   医生D
        Map map_user = new HashMap();
        map_user.put("phoneNumber", phone);
        map_user.put("newPassword", password);
        map_user.put("role", role);//病人 P   医生D
        map_user.put("smsCode", smsCode);
        String str_map_user = JSON.toJSONString(map_user);
        Log.i(TAG, "str_map_user" + str_map_user);

     /*   String str = SecurityHelper.jsSign(str_map_user, true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map_param = new HashMap();
        map_param.put("userData", rsaModel.getEncryptData());
        map_param.put("uuid", rsaModel.getUuid());
        map_param.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map_param.put("ios", null);
        String str_map_param = JSON.toJSONString(map_param);*/

        HttpUtils.modifyPwd(str_map_user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            Toast.makeText(mContext,response.getMessage(),Toast.LENGTH_LONG).show();
                            mContext.finish();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
                        }
                        //smsCodeHelper.onReceiveSmsCodeSuccess(response.getMessage());
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                        //smsCodeHelper.onReceiveSmsCodeFailure("onError: " + throwable.getMessage());
                        PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


}
