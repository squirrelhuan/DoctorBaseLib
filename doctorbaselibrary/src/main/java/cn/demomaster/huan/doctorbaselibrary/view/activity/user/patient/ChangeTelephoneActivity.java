package cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient;

import android.app.Activity;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.SmsCodeHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.StringVerifyUtil;

import com.alibaba.fastjson.JSON;

import cn.demomaster.huan.quickdeveloplibrary.widget.loader.LoadingDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;

public class ChangeTelephoneActivity extends BaseActivity implements View.OnClickListener {
    EditText etTelephone;
    EditText etSmscode;
    TextView tvGetSmscode;
    TextView tvReset;
    private String TAG = "CGQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_telephone);

        getActionBarLayoutOld().getRightView().setVisibility(View.GONE);
        getActionBarLayoutOld().setTitle("更换手机号");

        //changePhoneNum
        initViews();
    }

    public void initViews() {
        etTelephone = findViewById(R.id.et_telephone);
        etSmscode = findViewById(R.id.et_smscode);
        tvGetSmscode = findViewById(R.id.tv_get_smscode);
        tvReset = findViewById(R.id.tv_reset);

        tvGetSmscode.setOnClickListener(this);
        tvReset.setOnClickListener(this);
        initSmsCodeHelper();

        etTelephone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               checkParameter();
            }
        });

        etSmscode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkParameter();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_reset) {
           if(checkParameter()){
               changePhoneNum();
           }
        }
    }

    public boolean checkParameter() {
        tvReset.setEnabled(false);
        smsCode = etSmscode.getText().toString();
        phone = etTelephone.getText().toString();

        TextView[] views1 = {etTelephone, etSmscode};
        for (TextView editView : views1) {
            if (TextUtils.isEmpty(editView.getText().toString())) {
                editView.setError("不能为空");
                //PopToastUtil.ShowToast((Activity) mContext, desc + "不能为空");
                return false;
            }
        }

        if (!StringVerifyUtil.validateMobilePhone(etTelephone.getText().toString())) {
            //etTelephone.setError("请填写正确的手机号");
            return false;
        }
        tvReset.setEnabled(true);
        return true;
    }

    private String smsCode;
    private String phone;
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
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
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

    LoadingDialog loadingDialog;

    //发起网络请求获取验证码
    public void changePhoneNum() {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        if (role.equals("P")) {
            Map map_user = new HashMap();
            map_user.put("phoneNumber", phone);
            map_user.put("role", role);//病人 P   医生D
            map_user.put("smsCode", smsCode);
            String str_map_user = JSON.toJSONString(map_user);
            Log.i(TAG, "str_map_user" + str_map_user);

            String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
            SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
            Map map2 = new HashMap();
            map2.put("sessionId", SessionHelper.getSessionId());
            map2.put("uuid", rsaModel.getUuid());
            map2.put("token", rsaModel.getEncryptData());
            map2.put("ios", null);
            String map_str2 = JSON.toJSONString(map2);

            //Retrofit
            RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
            retrofitInterface.changePhoneNum(str_map_user)//yidao/patient/changePhoneNum
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<CommonApi>() {
                        @Override
                        public void onNext(@NonNull CommonApi response) {
                            Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                            if (response.getRetCode() == 0) {
                                PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
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
        } else {

            LoadingDialog.Builder builder = new LoadingDialog.Builder(mContext);
            loadingDialog = builder.setMessage("提交修改").setCanTouch(false).create();
            loadingDialog.show();

            Map map_user = new HashMap();
            map_user.put("phone", phone);
            map_user.put("smsCode", smsCode);
            map_user.put("ios", null);
            String str_map_user = JSON.toJSONString(map_user);
            Log.i(TAG, "str_map_user" + str_map_user);

            //String str = SecurityHelper.jsSign(str_map_user, true);
            String str = SecurityHelper.jsSignDouble(str_map_user, SessionHelper.getToken());
            SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
            map_user.put("token", rsaModel.getEncryptData2());
            map_user.put("data", rsaModel.getEncryptData1());
            map_user.put("uuid", rsaModel.getUuid());
            map_user.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
            map_user.put("ios", null);
            String str_map_param = JSON.toJSONString(map_user);

            //Retrofit
            RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
            //RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
            retrofitInterface.modifyProfile(str_map_param)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<CommonApi>() {
                        @Override
                        public void onNext(@NonNull CommonApi response) {
                            Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                            if (response.getRetCode() == 0) {
                                Toast.makeText(mContext, "修改成功！", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
                            }
                            loadingDialog.dismiss();
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
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            loadingDialog.dismiss();
                            Log.i(TAG, "onComplete: ");
                        }
                    });

        }
    }
}
