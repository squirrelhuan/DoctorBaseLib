package cn.demomaster.huan.doctorbaselibrary.view.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderDoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.chat.ChatWindow;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import com.alibaba.fastjson.JSON;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.Constant.ActivityResult_Parent_Refresh;

/**
 * 订单页面
 */
public class OrderListActivity extends BaseOrderListActivity {

    RecyclerView recyOrder;
    RadioGroup rgTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        getActionBarLayoutOld().setTitle("我的咨询");

        init();
    }

    private List<OrderModelApi> orders = new ArrayList();
    private OrderAdapter orderAdapter;
    private int dataType = 0;//数据类型
    private void init() {
        getData();
        recyOrder = findViewById(R.id.recy_order);
        rgTab = findViewById(R.id.rg_tab);
        orderAdapter = new OrderAdapter(this, orders, dataType);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recyOrder.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recyOrder.setAdapter(orderAdapter);
        //添加动画
        recyOrder.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        recyOrder.setItemAnimator(new DefaultItemAnimator());
        orderAdapter.setOnActionClickListener(new OnActionClickListener() {
            @Override
            public void onAccept(String requestId) {

            }
            @Override
            public void onResufe(String requestId, String sourceId) {
                showRefuseAppointTimeChangeDialog(requestId, sourceId, new OnActionResultListener() {
                    @Override
                    public void onSuccess() {
                        getData();
                    }
                });
            }
            @Override
            public void onResufeAndMotifyTime(String requestId) {

            }
            @Override
            public void onCanel(final String requestId) {
                showCancelDialog(requestId,dataType, new OnActionResultListener() {
                    @Override
                    public void onSuccess() {

                    }
                });
            }
            @Override
            public void onChart(String requestId, List<OrderDoctorModelApi.AdditionalInfo> additionalInfoList,boolean canChart) {
                ChatWindow.Builder builder = new ChatWindow.Builder(mContext, requestId, additionalInfoList, new ChatWindow.OnMessageListener() {
                    @Override
                    public void onSend(ChatWindow chatWindow) {
                        chatWindow.dismiss();
                        getData();
                    }
                });
                ChatWindow chatWindow = builder.build();
                chatWindow.showPopWin(mContext);
            }

            @Override
            public void onMotifyTime(String requestId) {
                OrderListActivity.this.showSelectTimeDialog(requestId, null, false, new BaseOrderListActivity.OnActionResultListener() {
                    @Override
                    public void onSuccess() {
                        getData();
                    }
                });
            }

            @Override
            public void approveAppointTimeChange(String requestId, String newTime, String sourceId) {
                OrderListActivity.this.approveAppointTimeChange(requestId, newTime, sourceId, new OnActionResultListener() {
                    @Override
                    public void onSuccess() {
                        getData();
                    }
                });
            }

            @Override
            public void refuseAppointTimeChange(String requestId, String sourceId) {
                showRefuseAppointTimeChangeDialog(requestId, sourceId, new OnActionResultListener() {
                    @Override
                    public void onSuccess() {
                        getData();
                    }
                });
            }

            @Override
            public void keepPreviousTime(String requestId) {
                OrderListActivity.this.keepPreviousTime(requestId, new OnActionResultListener() {
                    @Override
                    public void onSuccess() {
                        getData();
                    }
                });
            }
        });

        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_01) {//申请中
                    dataType = 0;
                }
                if (checkedId == R.id.rb_02) {//进行中
                    dataType = 1;
                }
                if (checkedId == R.id.rb_03) {//已结束
                    dataType = 2;
                }
                orders.clear();
                orderAdapter.setType(dataType);
                getData();
            }
        });
    }

    @Override
    public void getData() {
        showLoading("获取中");
        this.mBundle = this.getIntent().getExtras();
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.getAllTrxByState(dataType, map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            try {
                                //JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                                orders.clear();
                                List<OrderModelApi> doctors1 = JSON.parseArray(response.getData().toString(), OrderModelApi.class);
                                orders.addAll(doctors1);
                                orderAdapter.notifyDataSetChanged();
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
                        hideLoading();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        hideLoading();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ActivityResult_Parent_Refresh==resultCode){
            getData();
        }
    }
}
