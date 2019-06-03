package cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.TagFlowAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.FlowLayout;
import com.alibaba.fastjson.JSON;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;


/**
 * 添加过敏药
 */
public class AddAllergyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_allergy);

        init();
    }

    private String patientId="";
    private String KEY_STRING_REAL = "";
    private String KEY_STRING_ALL = "KEY_STRING_ALL";
    private List<Tag> arr_tag_all= new ArrayList<>();
    private List<String> arr_real= new ArrayList<>();
    private TextView tv_allergy;
    private FlowLayout fl_beill;
    TagFlowAdapter allergyAdapter;
    private String title;
    private int dataType =0;
    private void init() {

        mBundle=getIntent().getExtras();
        if(mBundle!=null&&mBundle.containsKey("PATIENT_ID")&&mBundle.containsKey("TAG_KEY_REAL")&&mBundle.containsKey("TAG_KEY_ALL")){
            patientId = mBundle.getString("PATIENT_ID","");
            KEY_STRING_REAL= mBundle.getString("TAG_KEY_REAL","");
            KEY_STRING_ALL= mBundle.getString("TAG_KEY_ALL","");
        }

        tv_allergy = findViewById(R.id.tv_allergy);
        if(KEY_STRING_ALL.equals("KEY_STRING_ALL_ALLERGY")){
            title = "添加过敏药物";
            tv_allergy.setText("选择添加您的过敏药物……");
            dataType = 0;
        }else {
            title = "添加疾病史";
            tv_allergy.setText("选择添加疾病史……");
            dataType = 1;
        }
        getActionBarLayoutOld().setTitle(title);
        fl_beill = findViewById(R.id.fl_tab);

        initTag();
    }

    private void checkTag(int index) {
        if(arr_real.contains(arr_tag_all.get(index).getName())){
            arr_real.remove(arr_tag_all.get(index).getName());
        }else {
            arr_real.add(arr_tag_all.get(index).getName());
        }
        SharedPreferencesHelper.getInstance().putString(KEY_STRING_REAL, JSON.toJSONString(arr_real));
        initTag();
        if(dataType==0){
            addAllergicDrugs();//过敏
        }else {
            addDiseaseHistory();//疾病史
        }
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
        map2.put("deputyUserId",patientId);
        StringBuffer allergicDrugsBuffer =new StringBuffer();
        for (String text:arr_real){
            allergicDrugsBuffer.append(text+",");
        }
        String allergicDrugs = allergicDrugsBuffer.substring(0,Math.max(0,allergicDrugsBuffer.length()-1));
        map2.put("allergicDrugs",allergicDrugs);
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
        map2.put("deputyUserId",patientId);
        StringBuffer diseaseHistoryBuffer =new StringBuffer();
        for (String text:arr_real){
            diseaseHistoryBuffer.append(text+",");
        }
        String diseaseHistory = diseaseHistoryBuffer.substring(0,Math.max(0,diseaseHistoryBuffer.length()-1));

        map2.put("diseaseHistory",diseaseHistory);
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

    private void initTag() {
        String str_all = SharedPreferencesHelper.getInstance().getString(KEY_STRING_ALL, null);
        if (str_all == null) {
            arr_tag_all = new ArrayList<>();
        } else {
            arr_tag_all = JSON.parseArray(str_all, Tag.class);
        }

        String str = SharedPreferencesHelper.getInstance().getString(KEY_STRING_REAL, null);
        if (TextUtils.isEmpty(str)) {
            arr_real= new ArrayList<>();
        } else {
            arr_real = JSON.parseArray(str, String.class);
        }

        Bundle bundle = new Bundle();
        bundle.putString("PATIENT_ID", patientId);
        bundle.putString("TAG_KEY_REAL", KEY_STRING_REAL);
        bundle.putString("TAG_KEY_ALL", KEY_STRING_ALL);
        bundle.putString("TITLE", title);
        bundle.putInt("dataType",dataType);
        allergyAdapter = new TagFlowAdapter(mContext, copyValue(arr_tag_all,arr_real),AddTagActivity.class,bundle);
        allergyAdapter.setOnItemClickListener(new TagFlowAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                checkTag(position);
            }
        });
        fl_beill.setAdapter(allergyAdapter);
    }
    private List<AddAllergyActivity.Tag> copyValue(List<AddAllergyActivity.Tag> arr_tag_all_beill, List<String> arr_beill) {
        for(AddAllergyActivity.Tag tag : arr_tag_all_beill){
            if(arr_beill.contains(tag.getName())){
                tag.setActive(true);
            }else {
                tag.setActive(false);
            }
        }
        return arr_tag_all_beill;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==200){
            initTag();

            if(dataType==0){
                addAllergicDrugs();//过敏
            }else {
                addDiseaseHistory();//疾病史
            }
        }
    }

    public static class Tag implements Serializable {
        private String name;
        private boolean isActive;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }
    }

}
