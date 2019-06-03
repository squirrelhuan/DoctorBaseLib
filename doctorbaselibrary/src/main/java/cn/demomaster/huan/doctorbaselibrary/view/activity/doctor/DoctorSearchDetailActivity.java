package cn.demomaster.huan.doctorbaselibrary.view.activity.doctor;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;

import cn.demomaster.huan.quickdeveloplibrary.view.loading.LoadingCircleView;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDActionDialog;
import com.alibaba.fastjson.JSON;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.DoctorSearchAdapter;
import cn.demomaster.huan.doctorbaselibrary.adapter.HistoryAdapter;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.DoctorSimpleModel;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.widget.FlowLayout;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class DoctorSearchDetailActivity extends BaseActivity implements View.OnClickListener {

    ImageView ivDeleteHistory;
    FlowLayout flSearchHistory;
    EditText etSearchKey;
    TextView tvSearchCancel;
    LinearLayout llHistory;
    RecyclerView recyDoctor;

    private DoctorSearchAdapter doctorSearchAdapter;
    private List<String> arr_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_search_detail);

        ivDeleteHistory= findViewById(R.id.iv_delete_history);
        flSearchHistory= findViewById(R.id.fl_search_history);
        etSearchKey= findViewById(R.id.et_search_key);
        tvSearchCancel= findViewById(R.id.tv_search_cancel);
        llHistory= findViewById(R.id.ll_history);
        recyDoctor= findViewById(R.id.recy_doctor);

        getActionBarLayoutOld().setTitle("按姓名找医生");

        ivDeleteHistory.setOnClickListener(this);
        etSearchKey.setOnClickListener(this);
        tvSearchCancel.setOnClickListener(this);
        init();
    }

    private List<DoctorSimpleModel> doctors = new ArrayList();
    private void init() {
        initHistory();

        doctorSearchAdapter = new DoctorSearchAdapter(this, doctors);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recyDoctor.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recyDoctor.setAdapter(doctorSearchAdapter);

        //添加动画
        recyDoctor.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        recyDoctor.setItemAnimator(new DefaultItemAnimator());

        etSearchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    llHistory.setVisibility(View.VISIBLE);
                    recyDoctor.setVisibility(View.GONE);
                } else {
                    llHistory.setVisibility(View.GONE);
                    recyDoctor.setVisibility(View.VISIBLE);
                    getData(s.toString());
                    tryToAddHistory(s.toString());
                }
            }
        });
    }

    private void tryToAddHistory(String s) {
        if (arr_history != null && !arr_history.contains(s)) {
            arr_history.add(s);
            SharedPreferencesHelper.getInstance().putString(key_history, JSON.toJSONString(arr_history));
        }
        initHistory();
    }

    private String key_history = "HISTOTY_SEARCH_DOCTOR";
    private HistoryAdapter historyAdapter;

    private void initHistory() {
        String str = SharedPreferencesHelper.getInstance().getString(key_history, null);
        if (str == null) {
            arr_history = new ArrayList<>();
        } else {
            arr_history = JSON.parseArray(str, String.class);
        }

  /*      SearchTabAdapter adapter;
        List<OptionsMenu.Menu> mulMenus = new ArrayList<>();
        mulMenus.clear();
        mulMenus.addAll(menus);
        adapter = new SearchTabAdapter(mContext, mulMenus);
*/

        historyAdapter = new HistoryAdapter(mContext, arr_history);
        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                etSearchKey.setText(arr_history.get(position));
                etSearchKey.setSelection(arr_history.get(position).length());//将光标移至文字末尾
            }
        });
        flSearchHistory.setAdapter(historyAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_search_cancel) {
            this.finish();
        }
        if (id == R.id.iv_delete_history) {
            deleteHistory();
        }

    }

    private void deleteHistory() {
        arr_history.clear();
        SharedPreferencesHelper.getInstance().putString(key_history, null);
        initHistory();
    }

    private void getData(String key) {
     /*   LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("列表获取中").setCanTouch(false).create();
        loadingDialog.show();*/
        final QDActionDialog qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(cn.demomaster.huan.doctorbaselibrary.R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("搜索中")
                .create();
        qdActionDialog.show();
        this.mBundle = this.getIntent().getExtras();
        HttpUtils.fuzzyQueryDoctor(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            try {
                                //JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                                doctors.clear();
                                List doctors1 = JSON.parseArray(response.getData().toString(), DoctorSimpleModel.class);
                                doctors.addAll(doctors1);
                                doctorSearchAdapter.notifyDataSetChanged();
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

}
