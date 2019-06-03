package cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.PatientInfoAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.UserModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientListActivity extends BaseActivity {

    private RecyclerView rcv_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        initView();
    }

    @Override
    public void onVerifyTokenSuccess() {
        super.onVerifyTokenSuccess();
        getPatientList();
    }

    private PatientInfoAdapter patientInfoAdapter;
    List<UserModelApi> userModelApis = new ArrayList<>();
    private void initView() {
        rcv_notification = findViewById(R.id.rcv_patient);
        userModelApis = new ArrayList<>();
        patientInfoAdapter = new PatientInfoAdapter(mContext, userModelApis);
        patientInfoAdapter.setOnItemClickListener(new PatientInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position, UserModelApi userModelApi) {
                Bundle bundle = new Bundle();
                bundle.putString("patientId", userModelApi.getUserId());
                startActivity(PatientInfoActivity.class, bundle);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        rcv_notification.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        rcv_notification.setAdapter(patientInfoAdapter);

        //添加动画
        rcv_notification.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        rcv_notification.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 获取关联用户
     */
    void getPatientList() {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, "getAllRelatedUsers=" + map_str2);
        HttpUtils.getAllRelatedUsers(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String uuid = jsonObject.get("uuid").toString();
                            String relatedUsers = jsonObject.get("relatedUsers").toString();
                            String userdata = SecurityHelper.decryptData(relatedUsers, uuid, SessionHelper.getClientPrivatekey());
                            Log.i(TAG, "userdata=" + userdata);
                            List<UserModelApi> userModelApiList = JSON.parseArray(userdata, UserModelApi.class);
                            userModelApis.clear();
                            userModelApis.addAll(userModelApiList);
                            patientInfoAdapter.notifyDataSetChanged();

                            UserHelper.getInstance().setCurrentPatient(userModelApis.get(0));
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
                        //loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        //loadingDialog.dismiss();
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200){
            getPatientList();
        }
    }

}
