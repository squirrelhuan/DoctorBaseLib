package cn.demomaster.huan.doctorbaselibrary.view.activity.doctor;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.adapter.DoctorAdapter;
import cn.demomaster.huan.doctorbaselibrary.adapter.EvaluateAdapter;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.QDDividerItemDecoration;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.loading.LoadingCircleView;
import cn.demomaster.huan.quickdeveloplibrary.widget.RatingBar;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDActionDialog;

import com.alibaba.fastjson.JSON;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.DoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.EvaluateModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医生评价页面
 */
public class DoctorEvaluateActivity extends BaseActivity {

    RatingBar ratingBarOverall;
    RecyclerView recyDoctor;
    private EvaluateAdapter evaluateAdapter;
    List<EvaluateModelApi.Evaluation> detailedEvaluations;
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
    TextView tv_evaluate_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_evaluate);

        getActionBarLayoutOld().setTitle("评价");

        init();
    }

    private DoctorModelApi doctorInfo;
    private void init() {
        ratingBarOverall = findViewById(R.id.ratingBar_overall);
        ratingBarOverall.setActivateCount(5);
        ratingBarOverall.setCanTouch(false);
        ratingBarOverall.setFloat(false);
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
        tv_evaluate_count = findViewById(R.id.tv_evaluate_count);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recyDoctor.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        detailedEvaluations = new ArrayList<>();
        evaluateAdapter = new EvaluateAdapter(this, detailedEvaluations);
        //设置Adapter
        recyDoctor.setAdapter(evaluateAdapter);
        //设置分割线使用的divider
        recyDoctor.addItemDecoration(new QDDividerItemDecoration(mContext, DividerItemDecoration.VERTICAL, getResources().getColor(R.color.main_color_gray_c3)));

        if (mBundle != null && mBundle.containsKey("doctorInfo")) {
            doctorInfo = (DoctorModelApi) mBundle.getSerializable("doctorInfo");
        }
        if (doctorInfo == null) {
            return;
        }
        getData();
    }

    private void refreshUI(EvaluateModelApi evaluateModelApi) {
        ratingBarOverall.setColor(mContext.getResources().getColor(R.color.main_color),mContext.getResources().getColor(R.color.white));
        ratingBarOverall.setActivateCount(Integer.valueOf(evaluateModelApi.getStarRanks().getOverallRank()));
        tvProfession01.setText(evaluateModelApi.getStarRanks().getProfessionRank5Star());
        tvProfession02.setText(evaluateModelApi.getStarRanks().getProfessionRank4Star());
        tvProfession03.setText(evaluateModelApi.getStarRanks().getProfessionRankBelow3Star());
        int p1 = Integer.valueOf(evaluateModelApi.getStarRanks().getProfessionRank5Star());
        int p2 = Integer.valueOf(evaluateModelApi.getStarRanks().getProfessionRank4Star());
        int p3 = Integer.valueOf(evaluateModelApi.getStarRanks().getProfessionRankBelow3Star());
        int p_all = p1 + p2 + p3;
        int process_p1 =0, process_p2=0, process_p3=0;
        if (p_all != 0) {
            process_p1 = (int) ((float) p1 / p_all * 100);
            process_p2 = (int) ((float) p2 / p_all * 100);
            process_p3 = (int) ((float) p3 / p_all * 100);
        }
        pbProfession01.setProgress(process_p1);
        pbProfession02.setProgress(process_p2);
        pbProfession03.setProgress(process_p3);

        tvService01.setText(evaluateModelApi.getStarRanks().getServiceRank5Star());
        tvService02.setText(evaluateModelApi.getStarRanks().getServiceRank4Star());
        tvService03.setText(evaluateModelApi.getStarRanks().getServiceRankBelow3Star());
        int s1 = Integer.valueOf(evaluateModelApi.getStarRanks().getServiceRank5Star());
        int s2 = Integer.valueOf(evaluateModelApi.getStarRanks().getServiceRank4Star());
        int s3 = Integer.valueOf(evaluateModelApi.getStarRanks().getServiceRankBelow3Star());
        int s_all = s1 + s2 + s3;
        int process_s1 =0, process_s2=0, process_s3=0;
        if (s_all != 0) {
            process_s1 = (int) ((float) s1 / s_all * 100);
            process_s2 = (int) ((float) s2 / s_all * 100);
            process_s3 = (int) ((float) s3 / s_all * 100);
        }
        pbService01.setProgress(process_s1);
        pbService02.setProgress(process_s2);
        pbService03.setProgress(process_s3);

        if(evaluateModelApi.getDetailedEvaluations()!=null){
            detailedEvaluations.clear();
            detailedEvaluations.addAll(evaluateModelApi.getDetailedEvaluations());
            tv_evaluate_count.setText("患者评价("+detailedEvaluations.size()+")");
            evaluateAdapter.notifyDataSetChanged();
        }
    }

    private void getData() {
        final QDActionDialog qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(cn.demomaster.huan.doctorbaselibrary.R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("评论获取中")
                .create();
        qdActionDialog.show();

        Map map2 = new HashMap();
        map2.put("doctorId", doctorInfo.getDoctorId());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.getHistoryEvaluation(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
            @Override
            public void onNext(@NonNull CommonApi response) {
                Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                if (response.getRetCode() == 0) {
                    try {
                        EvaluateModelApi evaluateModelApi = JSON.parseObject(response.getData().toString(), EvaluateModelApi.class);
                        refreshUI(evaluateModelApi);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    PopToastUtil.ShowToast((Activity) mContext, "评论获取失败：" + response.getMessage());
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

}
