package cn.demomaster.huan.doctorbaselibrary.view.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.loading.LoadingCircleView;
import cn.demomaster.huan.quickdeveloplibrary.widget.RatingBar;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDActionDialog;
import com.alibaba.fastjson.JSON;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;

/**
 * 用户端
 * 订单详情
 */
public class OrderDetailActivity extends BaseActivity {

    private String trxId;
    private TextView tv_order_name,tv_order_patient_name,tv_order_doctor_name,tv_order_appointment_time,tv_order_appointment_duration
            ,tv_order_appointment_fee,tv_order_desc
            ,tv_order_diseaseName,tv_order_diagnoseAdvise,tv_order_operation;
    private RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        getActionBarLayoutOld().setTitle("订单详情");
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null&&bundle.containsKey("trxId")){
            trxId = bundle.getString("trxId");
        }

        tv_order_name = findViewById(R.id.tv_order_name);
        tv_order_patient_name = findViewById(R.id.tv_order_patient_name);
        tv_order_doctor_name = findViewById(R.id.tv_order_doctor_name);
        tv_order_appointment_time = findViewById(R.id.tv_order_appointment_time);
        tv_order_appointment_duration = findViewById(R.id.tv_order_appointment_duration);
        tv_order_appointment_fee = findViewById(R.id.tv_order_appointment_fee);
        tv_order_desc = findViewById(R.id.tv_order_desc);
        ratingBar = findViewById(R.id.ratingBar_satisfaction);
        ratingBar.setActivateCount(5);
        ratingBar.setFloat(false);
        ratingBar.setBackResourceId(R.mipmap.ic_seekbar_star_normal);
        ratingBar.setFrontResourceId(R.mipmap.ic_seekbar_star_selected);
        ratingBar.setUseCustomDrable(true);

        tv_order_diseaseName = findViewById(R.id.tv_order_diseaseName);
        tv_order_diagnoseAdvise = findViewById(R.id.tv_order_diagnoseAdvise);
        tv_order_operation = findViewById(R.id.tv_order_operation);
        getData();
    }

    private void refresh(OrderDetailModel model) {
        tv_order_name.setText(model.getRequestName().replace("_","   "));
        tv_order_patient_name.setText(model.getName());
        tv_order_doctor_name.setText(model.getDoctorName());
        tv_order_appointment_fee.setText(model.getFee()+"元");
        tv_order_diseaseName.setText(model.getDisease()+"");
        tv_order_diagnoseAdvise.setText(model.getDiagnoseAdvise()+"");
        tv_order_operation.setText(model.getOperation()+"");
        tv_order_appointment_duration.setText(model.getDuration());
        tv_order_appointment_time.setText(model.getAppointmentTime());
    }

    //获取订单详情页
    public void getData() {
        final QDActionDialog qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(cn.demomaster.huan.doctorbaselibrary.R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("获取中")
                .create();
        qdActionDialog.show();

        this.mBundle = this.getIntent().getExtras();
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("trxId", trxId);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2); //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        retrofitInterface.getTrxDetailedInfoForPatient(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            try {
                                OrderDetailModel model = JSON.parseObject(response.getData().toString(),OrderDetailModel.class);
                                refresh(model);
                                // JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                                //orders.clear();
                               // List<OrderDoctorModelApi> doctors1 = JSON.parseArray(response.getData().toString(), OrderDoctorModelApi.class);
                                ///orders.addAll(doctors1);
                               // orderAdapter.notifyDataSetChanged();
                                //String token = jsonObject.get("token").toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                        qdActionDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        qdActionDialog.dismiss();
                    }
                });
    }
    public static class OrderDetailModel{

        /**
         * name : 褚*庆
         * gender : M
         * age : 26
         * address : 上海市 闵行区 罗锦路113
         * appointmentTime : 2019-04-09 12:15:00
         * descByPatient : 测试1
         * descUrlByPatient :
         * trxId : 69
         * requestName : 全科_20190409120355
         * createdAt : 2019-04-09
         * phoneNum : 166****5136
         * startAt : 2019-04-09 12:04:59
         * endAt : 2019-04-09 12:06:23
         * duration : 1分钟24秒
         * fee : 1800
         * disease : 骨盆倾斜
         * operation : 肢体动脉压测量
         * diagnoseAdvise : ‘处理论证
         * baseCharge : 1800 rmb
         * extraCharge : 56 rmb
         */

        private String name;
        private String doctorName;
        private String gender;
        private int age;
        private String address;
        private String appointmentTime;
        private String descByPatient;
        private String descUrlByPatient;
        private int trxId;
        private String requestName;
        private String createdAt;
        private String phoneNum;
        private String startAt;
        private String endAt;
        private String duration;
        private String fee;
        private String disease;
        private String operation;
        private String diagnoseAdvise;
        private String baseCharge;
        private String extraCharge;
        private String isEvaluationCompleteForP;

        public String getIsEvaluationCompleteForP() {
            return isEvaluationCompleteForP;
        }

        public void setIsEvaluationCompleteForP(String isEvaluationCompleteForP) {
            this.isEvaluationCompleteForP = isEvaluationCompleteForP;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAppointmentTime() {
            return appointmentTime;
        }

        public void setAppointmentTime(String appointmentTime) {
            this.appointmentTime = appointmentTime;
        }

        public String getDescByPatient() {
            return descByPatient;
        }

        public void setDescByPatient(String descByPatient) {
            this.descByPatient = descByPatient;
        }

        public String getDescUrlByPatient() {
            return descUrlByPatient;
        }

        public void setDescUrlByPatient(String descUrlByPatient) {
            this.descUrlByPatient = descUrlByPatient;
        }

        public int getTrxId() {
            return trxId;
        }

        public void setTrxId(int trxId) {
            this.trxId = trxId;
        }

        public String getRequestName() {
            return requestName;
        }

        public void setRequestName(String requestName) {
            this.requestName = requestName;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getStartAt() {
            return startAt;
        }

        public void setStartAt(String startAt) {
            this.startAt = startAt;
        }

        public String getEndAt() {
            return endAt;
        }

        public void setEndAt(String endAt) {
            this.endAt = endAt;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getDisease() {
            return disease;
        }

        public void setDisease(String disease) {
            this.disease = disease;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getDiagnoseAdvise() {
            return diagnoseAdvise;
        }

        public void setDiagnoseAdvise(String diagnoseAdvise) {
            this.diagnoseAdvise = diagnoseAdvise;
        }

        public String getBaseCharge() {
            return baseCharge;
        }

        public void setBaseCharge(String baseCharge) {
            this.baseCharge = baseCharge;
        }

        public String getExtraCharge() {
            return extraCharge;
        }

        public void setExtraCharge(String extraCharge) {
            this.extraCharge = extraCharge;
        }
    }

}
