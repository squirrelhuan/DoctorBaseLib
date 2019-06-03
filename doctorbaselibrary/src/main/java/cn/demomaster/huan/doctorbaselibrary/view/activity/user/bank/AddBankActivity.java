package cn.demomaster.huan.doctorbaselibrary.view.activity.user.bank;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.loader.LoadingDialog;
import com.alibaba.fastjson.JSON;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;

/**
 * 银行卡添加页面
 */
public class AddBankActivity extends BaseActivity implements View.OnClickListener {

    TextView tv_add_card;
    EditText et_userName;
    EditText et_card_number;
    private boolean isEdit = false;
    private AddRessModel addRessModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bank);

        tv_add_card = findViewById(R.id.tv_add_card);
        et_userName = findViewById(R.id.et_userName);
        et_card_number = findViewById(R.id.et_card_number);


        if (mBundle != null && mBundle.containsKey("isEdit") && mBundle.containsKey("bank") && mBundle.getBoolean("isEdit", false)) {
            getActionBarLayoutOld().setTitle(getResources().getString(R.string.add_bank));//Edit_new_address
            addRessModel = (AddRessModel) mBundle.getSerializable("bank");
            et_card_number.setText(addRessModel.getDetailAddress());
            isEdit = true;
        } else {
            getActionBarLayoutOld().setTitle(getResources().getString(R.string.add_bank));
        }
        tv_add_card.setOnClickListener(this);

        getAccountName();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.tv_add_card){
            tryToSubmit();
        }
    }

    private void tryToSubmit() {
        if (et_userName.getText() == null || TextUtils.isEmpty(et_userName.getText().toString())) {
            PopToastUtil.ShowToast(mContext, "请填写持卡人姓名");
            return;
        }
        if (et_card_number.getText() == null || TextUtils.isEmpty(et_card_number.getText().toString())) {
            PopToastUtil.ShowToast(mContext, "请填写卡号");
            return;
        }
        bankAccount = et_card_number.getText().toString();
        addBank( isEdit);
    }


    public void getAccountName() {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        retrofitInterface.getAccountName(body)//yidao/patient/changePhoneNum
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            et_userName.setText(response.getData().toString());
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
                        //smsCodeHelper.onReceiveSmsCodeFailure("onError: " + throwable.getMessage());
                        PopToastUtil.ShowToast((Activity) mContext, "修改失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    private String bankAccount="";
    public void addBank( boolean isEdit) {
        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("正在提交").setCanTouch(false).create();
        loadingDialog.show();

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("bankAccount", bankAccount);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        retrofitInterface.bindBankAccount(body)
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
                            Toast.makeText(mContext,"失败：" + response.getMessage(),Toast.LENGTH_LONG).show();
                            //PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
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
}
