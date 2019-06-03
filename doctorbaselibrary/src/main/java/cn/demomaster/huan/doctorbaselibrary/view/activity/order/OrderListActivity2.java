package cn.demomaster.huan.doctorbaselibrary.view.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter2;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderModel2Api;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.QDLogger;
import cn.demomaster.huan.quickdeveloplibrary.view.loading.LoadingCircleView;
import cn.demomaster.huan.quickdeveloplibrary.view.tabmenu.TabMenuLayout;
import cn.demomaster.huan.quickdeveloplibrary.view.tabmenu.TabMenuModel;
import cn.demomaster.huan.quickdeveloplibrary.view.tabmenu.TabRadioGroup;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDActionDialog;
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
 * 反馈订单选择页
 */
public class OrderListActivity2 extends BaseOrderListActivity {

    RecyclerView recyOrder;
    TabMenuLayout tabMenuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list2);

        tabMenuLayout= findViewById(R.id.tab_menu_layout);
        recyOrder = findViewById(R.id.recy_order);
        getActionBarLayoutOld().setTitle("选择订单");

        init();
    }

    private List<OrderModelApi> doctors1;
    private List<OrderModel2Api> orders = new ArrayList();
    private OrderAdapter2 orderAdapter2;
    private int dataType = 0;

    private void init() {
        initTabMenu();
        getData();
        orderAdapter2 = new OrderAdapter2(this, orders, dataType);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recyOrder.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recyOrder.setAdapter(orderAdapter2);

        //添加动画
        recyOrder.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        recyOrder.setItemAnimator(new DefaultItemAnimator());
        orderAdapter2.setOnItemClickListener(new OrderAdapter2.OnItemClickListener() {
            @Override
            public void onClick(int adapterPosition) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                OrderModel2Api orderModelApi = orders.get(adapterPosition);
                bundle.putSerializable("orderModelApi", orderModelApi);
                intent.putExtras(bundle);
                setResult(200, intent);
                finish();
            }
        });
    }

    private void initTabMenu() {
        /*********************                数据初始化                   ************************/
        final String[] aaa = {"1个月", "3个月", "6个月","一年","一年以上"};
        final String[] bbb = { "进行中", "已结束"};//"申请中",
        List<Integer> selectData_a = new ArrayList<>();
        selectData_a.add(0);
        List<Integer> selectData_b = new ArrayList<>();
        selectData_b.add(0);

        List<Integer> selectData_d = new ArrayList<>();
        selectData_d.add(0);
        final List<TabMenuModel> tabSelectModels = new ArrayList<>();//用来存放初始化选项的状态
        TabMenuModel tabMenuModel = new TabMenuModel("时间", aaa, selectData_a);
        tabMenuModel.setTabButtonView(new TButton(this));
        tabMenuModel.setColorContent(mContext.getResources().getColor(R.color.main_color),mContext.getResources().getColor(R.color.main_color_gray_46));
        tabSelectModels.add(tabMenuModel);

        TabMenuModel tabMenuModel1 = new TabMenuModel("订单状态", bbb, 1, selectData_b);
        tabMenuModel1.setTabButtonView(new TButton(this));
        tabMenuModel1.setColorContent(mContext.getResources().getColor(R.color.main_color),mContext.getResources().getColor(R.color.main_color_gray_46));
        tabSelectModels.add(tabMenuModel1);

        /*********************      组建初始化                   ************************/
        tabMenuLayout = findViewById(R.id.tab_menu_layout);
        tabMenuLayout.setTabDividerResId(R.layout.tab_layout_driver);
        tabMenuLayout.setData(tabSelectModels, new TabMenuLayout.TabMenuInterface() {
            @Override
            public String onSelected(TabRadioGroup.TabRadioButton tabButton,int tabIndex, int position) {
                //PopToastUtil.ShowToast((Activity) mContext, "" + tabIndex + ":" + position);
                String status[] = {"ongoing" , "closed"};
                String trxSpanStr[] = {"month" , "quarter","halfYear", "year", "all"};
                QDLogger.i("tabIndex="+tabIndex+", position="+position);
                switch (tabIndex){
                    case 0://评价
                        trxSpan = trxSpanStr[position];
                        tabButton.setTabName(aaa[position]);
                        break;
                    case 1:
                        dataType = position;
                       // orders.clear();
                        //orderAdapter2.setType(dataType);
                        trxStatus =status[position];
                        tabButton.setTabName(bbb[position]);
                        break;
                }
                getData();
                tabMenuLayout.getPopupWindow().dismiss();
                return null;
            }
        });
    }

    @Override
    public void getData() {
        //super.getData();
        if(AppConfig.getInstance().isPatient()){
            getTrxInfoP();
        }else {
            getTrxInfoD();
        }
    }

    private String trxStatus = null;
    private String trxSpan = null;
    private void getTrxInfoD(){
        final QDActionDialog qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("获取中")
                .create();
        qdActionDialog.show();

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        map2.put("trxStatus", trxStatus);//ongoing ， closed
        map2.put("trxSpan", trxSpan);//month, quarter, halfYear, year, all

        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        retrofitInterface.getTrxInfoD(body)//
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            orders.clear();
                            List<OrderModel2Api> doctors1 = JSON.parseArray(response.getData().toString(), OrderModel2Api.class);
                            orders.addAll(doctors1);
                            orderAdapter2.notifyDataSetChanged();
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
                        PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        qdActionDialog.dismiss();
                    }
                });
    }
    private void getTrxInfoP(){
        final QDActionDialog qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("获取中")
                .create();
        qdActionDialog.show();

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        map2.put("trxStatus", trxStatus);//ongoing ， closed
        map2.put("trxSpan", trxSpan);//month, quarter, halfYear, year, all

        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        retrofitInterface.getTrxInfoP(body)//
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //et_userName.setText(response.getData().toString());

                            orders.clear();
                            List<OrderModel2Api> doctors1 = JSON.parseArray(response.getData().toString(), OrderModel2Api.class);
                            orders.addAll(doctors1);
                            orderAdapter2.notifyDataSetChanged();

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
                        PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        qdActionDialog.dismiss();
                    }
                });
    }

    public class TButton extends TabRadioGroup.TabRadioButton {
        private TextView tv_tab_name;
        private View view;

        public TButton(Context context) {
            super(context);
        }

        public TButton(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public TButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void setState(Boolean state) {
            Drawable drawable;
            if (!state) {
                tv_tab_name.setTextColor(getResources().getColor(R.color.main_color_gray_46));
                // 使用代码设置drawableleft
                drawable = getResources().getDrawable(R.mipmap.ic_arrow_bottom_normal);
            } else {
                tv_tab_name.setTextColor(getResources().getColor(R.color.main_color));
                // 使用代码设置drawableleft
                drawable = getResources().getDrawable(R.mipmap.ic_arrow_bottom_selected);
                //tv_tab_name.setTextAppearance(getContext(),R.style.);
            }
            // / 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
            tv_tab_name.setCompoundDrawables(null, null, drawable, null);
        }

        @Override
        public void setTabName(String tabName) {
            tv_tab_name.setText(tabName);
        }

        @Override
        public void initView(Context context) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_tab_menu_layout, null);
            tv_tab_name = (TextView) view.findViewById(R.id.tv_tab_name);
            view.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            this.addView(view);
        }
    }

}
