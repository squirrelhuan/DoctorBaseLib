package cn.demomaster.huan.doctorbaselibrary.view.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.CouponSelectAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.CouponModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import com.alibaba.fastjson.JSON;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.*;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;

/**
 * 优惠券页面
 */
public class CouponSelectActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CouponSelectAdapter couponSelectAdapter;
    //private CheckBox cb_use_coupon;
    private Intent resultIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_selector);
        getActionBarLayoutOld().setTitle("优惠券");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("coupons")) {
            checkedCouponList = (HashMap<String, CouponModelApi>) bundle.getSerializable("coupons");
        }
        recyclerView = findViewById(R.id.recyclerView);
        if (checkedCouponList == null) {
            checkedCouponList = new LinkedHashMap<>();
        }
        mDatas = new ArrayList<>();
        couponSelectAdapter = new CouponSelectAdapter(mDatas, checkedCouponList, new CouponSelectAdapter.OnItemClicked() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(mContext, CouponDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("coupon", mDatas.get(position));
                intent.putExtras(bundle);
                startActivityForResult(intent, 100);
            }

            @Override
            public void onCheckChanged(int index) {
                reSetCheckedList(index);
                reSetResult();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recyclerView.setAdapter(couponSelectAdapter);

        //添加动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getDiscountTickets();
    }

    private void reSetResult() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("coupons", checkedCouponList);
        resultIntent.putExtras(bundle);
        setResult(200, resultIntent);
    }

    private boolean multipleUsage = false;//是否支持多个
    private void reSetCheckedList(int index) {
        if (multipleUsage == false) {//单选
            if (checkedCouponList.containsKey(mDatas.get(index).getId())) {
                checkedCouponList.remove(checkedCouponList.get(mDatas.get(index).getId()));
            } else {
                checkedCouponList.clear();
                checkedCouponList.put(mDatas.get(index).getId(), mDatas.get(index));
            }
        } else {//多选
            if (checkedCouponList.containsKey(mDatas.get(index).getId())) {
                checkedCouponList.remove(checkedCouponList.get(mDatas.get(index).getId()));
            } else {
                checkedCouponList.put(mDatas.get(index).getId(), mDatas.get(index));
            }
        }
        refreshAdapter();
    }

    private void refreshAdapter() {
        couponSelectAdapter.notifyDataSetChanged();
       // HashMap hashMap = new HashMap<>();
        //.putAll(checkedCouponList);
        //couponSelectAdapter.setCheckedCouponList(checkedCouponList);
        /*checkedCouponList.clear();
        couponSelectAdapter.notifyDataSetChanged();
        checkedCouponList.putAll(hashMap);
        hashMap.clear();
        couponSelectAdapter.notifyDataSetChanged();*/
    }

    private void addCoupon(CouponModelApi couponModelApi) {
        if (multipleUsage == false) {//单选
            checkedCouponList.clear();
            checkedCouponList.put(couponModelApi.getId(), couponModelApi);
        } else {//多选
            if (!checkedCouponList.containsKey(couponModelApi.getId())) {
                checkedCouponList.put(couponModelApi.getId(), couponModelApi);
            }
        }
        refreshAdapter();
    }

    private HashMap<String, CouponModelApi> checkedCouponList;
    private List<CouponModelApi> mDatas;

    /**
     * 获取优惠券列表
     */
    public void getDiscountTickets() {
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
        retrofitInterface.getDiscountTickets(body)//yidao/patient/changePhoneNum
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //et_userName.setText(response.getData().toString());
                            mDatas.clear();
                            List<CouponModelApi> couponModelApis = JSON.parseArray(response.getData().toString(), CouponModelApi.class);
                            mDatas.addAll(couponModelApis);
                            couponSelectAdapter.notifyDataSetChanged();
                        } else {
                            PopToastUtil.ShowToast(mContext, "失败：" + response.getMessage());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (data == null) return;
                Bundle bundle = data.getExtras();
                if (bundle != null && bundle.containsKey("coupon")) {
                    CouponModelApi couponModelApi;
                    couponModelApi = (CouponModelApi) bundle.getSerializable("coupon");
                    addCoupon(couponModelApi);
                    Log.i("CGQ", "checkedCouponList.size=" + checkedCouponList.size());
                }
                reSetResult();
                break;
        }
    }
}
