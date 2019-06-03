package cn.demomaster.huan.doctorbaselibrary.view.activity.order;

import android.app.Activity;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.EvaluateModelApi;
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
 * 医生评价页面
 */
public class EvaluateActivity_Doctor extends BaseActivity {

    RatingBar ratingBarOverall;
    RecyclerView recyDoctor;
    ProgressBar pbProfession01;
    TextView tvProfession01;
    ProgressBar pbProfession02;
    TextView tvProfession02;
    ProgressBar pbProfession03;
    TextView tvProfession03;
    ProgressBar pbService01;
    TextView tvService01;
    ProgressBar pbService02;
    TextView tvService02;
    ProgressBar pbService03;
    TextView tvService03;
    Button btn_submit;
    EditText et_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate2);

        getActionBarLayoutOld().setTitle("我的评价");
        init();
    }

    //private DoctorModelApi doctorInfo;
    private void init() {
        ratingBarOverall = findViewById(R.id.ratingBar_overall);
        ratingBarOverall.setActivateCount(5);
        ratingBarOverall.setFloat(false);
        ratingBarOverall.setCanTouch(true);
        ratingBarOverall.setBackResourceId(R.mipmap.ic_seekbar_star_normal);
        ratingBarOverall.setFrontResourceId(R.mipmap.ic_seekbar_star_selected);
        ratingBarOverall.setUseCustomDrable(true);
        recyDoctor = findViewById(R.id.recy_doctor);
        pbProfession01 = findViewById(R.id.pb_profession_01);
        tvProfession01 = findViewById(R.id.tv_profession_01);
        pbProfession02 = findViewById(R.id.pb_profession_02);
        tvProfession02 = findViewById(R.id.tv_profession_02);
        pbProfession03 = findViewById(R.id.pb_profession_03);
        tvProfession03 = findViewById(R.id.tv_profession_03);
        pbService01 = findViewById(R.id.pb_service_01);
        tvService01 = findViewById(R.id.tv_service_01);
        pbService02 = findViewById(R.id.pb_service_02);
        tvService02 = findViewById(R.id.tv_service_02);
        pbService03 = findViewById(R.id.pb_service_03);
        tvService03 = findViewById(R.id.tv_service_03);

        et_description = findViewById(R.id.et_description);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToSubmit();
            }
        });
        if (mBundle != null && mBundle.containsKey("trxId")) {
            trxId = mBundle.getString("trxId", "");
        }
    }

    String merit = "";
    String moreInfo = "";
    private String trxId;
    private void tryToSubmit() {
        moreInfo = et_description.getText().toString();
        merit = ratingBarOverall.getProgressInteger() + "";
        if (trxId==null){
            Toast.makeText(mContext, "订单号不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(moreInfo)) {
            Toast.makeText(mContext, "请输入评价描述", Toast.LENGTH_LONG).show();
            return;
        }
        evaluateService();
    }

    public void evaluateService() {
        final QDActionDialog qdActionDialog;
        qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("提交中")
                .create();
        qdActionDialog.show();

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        //{“ sessionId”:”7”,”token”:”abcdefg”,"requestId":"1";"professionMerit":"4";"serviceMerit":"3","moreInfo":"默认好评"}

        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        map2.put("trxId", trxId);
        map2.put("merit", merit);
        map2.put("moreInfo", moreInfo);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        retrofitInterface.evaluatePatient(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            setResult(200);
                            finish();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "fail：" + response.getMessage());
                        }
                        qdActionDialog.dismiss();
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

}
