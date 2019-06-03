package cn.demomaster.huan.doctorbaselibrary.view.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.CouponAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;

/**
 * 优惠券页面
 */
public class CouponActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CouponAdapter couponAdapter;
   // private CheckBox cb_use_coupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        getActionBarLayoutOld().setTitle("优惠券");
        /*cb_use_coupon = findViewById(R.id.cb_use_coupon);
        cb_use_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        recyclerView = findViewById(R.id.recyclerView);
        mDatas = new ArrayList<>();
        couponAdapter = new CouponAdapter(mDatas, new CouponAdapter.OnItemClicked() {
            @Override
            public void onItemClicked(View view, int position, AddRessModel addRessModel) {

            }

            @Override
            public void onDeleteButtonClicked(int index) {

            }

            @Override
            public void onEditButtonClicked(AddRessModel addRessModel) {

            }

            @Override
            public void onSubmitButtonClicked() {

            }
        });
        //orderAdapter = new OrderTimeListAdapter(this, data,1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recyclerView.setAdapter(couponAdapter);
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

    private List<CouponModelApi> mDatas;
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
                            couponAdapter.notifyDataSetChanged();
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
                        PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }
}
