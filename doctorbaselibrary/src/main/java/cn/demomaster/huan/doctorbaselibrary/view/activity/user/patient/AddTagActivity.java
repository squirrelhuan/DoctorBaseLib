package cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import com.alibaba.fastjson.JSON;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;

/**
 * 添加现在患疾病
 */
public class AddTagActivity extends BaseActivity {

    private EditText et_tag;
    private TextView tv_add_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        //getActionBarLayout().get
        init();
    }

    private String patientId = "";
    private String KEY_STRING_REAL = "KEY_STRING_REAL";
    private String KEY_STRING_ALL = "KEY_STRING_ALL";
    private String title;
    private List<String> arr_real = new ArrayList<>();
    private List<AddAllergyActivity.Tag> arr_tag_all = new ArrayList<>();
    private int dataType = 0;

    private void init() {

        mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey("PATIENT_ID") && mBundle.containsKey("TAG_KEY_REAL") && mBundle.containsKey("TAG_KEY_ALL")) {
            patientId = mBundle.getString("PATIENT_ID", "");
            KEY_STRING_REAL = mBundle.getString("TAG_KEY_REAL", "");
            KEY_STRING_ALL = mBundle.getString("TAG_KEY_ALL", "");
            title = mBundle.getString("TITLE", "");
            dataType = mBundle.getInt("dataType");
        }
        getActionBarLayoutOld().setTitle(title);

        et_tag = findViewById(R.id.et_tag);
        tv_add_tag = findViewById(R.id.tv_add_tag);
        tv_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });

        if (dataType == 0) {
            et_tag.setHint("在此输入您的过敏药物……");
        } else {
            et_tag.setHint("在此输入您的患病史……");
        }

        String str_all = SharedPreferencesHelper.getInstance().getString(KEY_STRING_ALL, null);
        if (str_all == null) {
            arr_tag_all = new ArrayList<>();
        } else {
            arr_tag_all = JSON.parseArray(str_all, AddAllergyActivity.Tag.class);
        }

        String str = SharedPreferencesHelper.getInstance().getString(KEY_STRING_REAL, null);
        if (TextUtils.isEmpty(str)) {
            arr_real = new ArrayList<>();
        } else {
            arr_real = JSON.parseArray(str, String.class);
        }
    }

    private void addTag() {
        if (TextUtils.isEmpty(et_tag.getText()) || TextUtils.isEmpty(et_tag.getText().toString().trim())) {
            PopToastUtil.ShowToast(mContext, "请先输入再操作");
            return;
        }
        String tagc = et_tag.getText().toString();
        if (!arr_real.contains(tagc)) {
            arr_real.add(tagc);
        }
        SharedPreferencesHelper.getInstance().putString(KEY_STRING_REAL, JSON.toJSONString(arr_real));
        String str_all = SharedPreferencesHelper.getInstance().getString(KEY_STRING_ALL, null);
        if (str_all == null) {
            arr_tag_all = new ArrayList<>();
        } else {
            arr_tag_all = JSON.parseArray(str_all, AddAllergyActivity.Tag.class);
        }
        if (!containsTag(arr_tag_all, tagc)) {
            AddAllergyActivity.Tag tag = new AddAllergyActivity.Tag();
            tag.setActive(false);
            tag.setName(tagc);
            arr_tag_all.add(tag);
        }
        SharedPreferencesHelper.getInstance().putString(KEY_STRING_ALL, JSON.toJSONString(arr_tag_all));

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        AddAllergyActivity.Tag tag = new AddAllergyActivity.Tag();
        tag.setActive(true);
        tag.setName(et_tag.getText().toString());
        bundle.putSerializable("TAG", tag);
        intent.putExtras(bundle);
        setResult(200, intent);

        if (dataType == 0) {
            addAllergicDrugs();//过敏
        } else {
            addDiseaseHistory();//疾病史
        }

        //finish();
    }

    boolean containsTag(List<AddAllergyActivity.Tag> arr_tag_all, String tagc) {
        List<String> strings = new ArrayList<>();
        for (AddAllergyActivity.Tag tag : arr_tag_all) {
            strings.add(tag.getName());
        }
        return strings.contains(tagc);
    }


    /**
     * 添加过敏药物
     */
    private void addAllergicDrugs() {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("deputyUserId", patientId);
        StringBuffer allergicDrugsBuffer = new StringBuffer();
        for (String text : arr_real) {
            allergicDrugsBuffer.append(text + ",");
        }
        String allergicDrugs = allergicDrugsBuffer.substring(0, Math.max(0, allergicDrugsBuffer.length() - 1));
        map2.put("allergicDrugs", allergicDrugs);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        retrofitInterface.addAllergicDrugs(body)//yidao/patient/changePhoneNum
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            finish();
                            //et_userName.setText(response.getData().toString());
                            // JSON.parseArray(response.getData().toString(), CouponModel.class);
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
                        PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * 添加历史疾病
     */
    private void addDiseaseHistory() {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("deputyUserId", patientId);
        StringBuffer diseaseHistoryBuffer = new StringBuffer();
        for (String text : arr_real) {
            diseaseHistoryBuffer.append(text + ",");
        }
        String diseaseHistory = diseaseHistoryBuffer.substring(0, Math.max(0, diseaseHistoryBuffer.length() - 1));

        map2.put("diseaseHistory", diseaseHistory);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        retrofitInterface.addDiseaseHistory(body)//yidao/patient/changePhoneNum
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            finish();
                            //et_userName.setText(response.getData().toString());
                            // JSON.parseArray(response.getData().toString(), CouponModel.class);
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
                        PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }
}
