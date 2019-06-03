package cn.demomaster.huan.doctorbaselibrary.view.activity.doctor;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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
import android.view.*;
import android.widget.TextView;

import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.loading.LoadingCircleView;
import cn.demomaster.huan.quickdeveloplibrary.view.tabmenu.TabMenuLayout;
import cn.demomaster.huan.quickdeveloplibrary.view.tabmenu.TabMenuModel;
import cn.demomaster.huan.quickdeveloplibrary.view.tabmenu.TabRadioGroup;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDActionDialog;

import com.alibaba.fastjson.JSON;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.DoctorAdapter;
import cn.demomaster.huan.doctorbaselibrary.application.MyApp;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.DepartMentModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.DoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.widget.time.DateTimePickerPopView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorListActivity extends BaseActivity {

    RecyclerView recyDoctor;
    RecyclerView recy_doctor_result;
    TabMenuLayout tabMenuLayout;

    private DoctorAdapter doctorAdapterResult;
    private DoctorAdapter doctorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        recy_doctor_result= findViewById(R.id.recy_doctor_result);
        recyDoctor= findViewById(R.id.recy_doctor);
        tabMenuLayout= findViewById(R.id.tab_menu_layout);

        getActionBarLayoutOld().setTitle("专家列表");

        init();
        initTabMenu();
    }

    List<DepartMentModel> getDataByType() {
        List<DepartMentModel> lists = new ArrayList<>();
        Cursor cursor = MyApp.getInstance().db.query("inner_department_category", new String[]{"id", "categoryName", "categoryCode", "type", "iconName"}, "isValid=? ORDER BY \"index\" ASC", new String[]{"1"}, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("categoryName"));
            String code = cursor.getString(cursor.getColumnIndex("categoryCode"));
            //String type = cursor.getString(cursor.getColumnIndex("type"));
            String iconName = cursor.getString(cursor.getColumnIndex("iconName"));
            DepartMentModel departMentModel = new DepartMentModel();
            departMentModel.setName(name);
            departMentModel.setIconName(iconName);
            departMentModel.setCode(code);
            lists.add(departMentModel);
            //Log.i(TAG, "query------->" + "categoryName：" + name + " " + ",categoryCode：" + code + ",iconName：" + iconName);
        }
        return lists;
    }

    private List<DepartMentModel> categoryList = new ArrayList<>();
    String[] evaluationRankTypes ={"OVERALL", "PROFESSION", "SERVICE"};//OVERALL("综合评价","OVERALL"),PROFESSION("专业评价","PROFESSION"),SERVICE("态度评价","SERVICE");
    String[] titles ={null,"1","2","3"};//ARCHIATER("1","主任医师"),VICE_ARCHIATER("2","副主任医师"),VISITING_STAFF("3","主治医师");
    private void initTabMenu() {
        /*********************                数据初始化                   ************************/
        final String[] aaa = {"综合评价", "专业水平", "服务态度"};
        final String[] bbb = {"全部", "主任医师", "副主任医师", "主治医师"};
        categoryList.addAll(getDataByType());
        final String[] ccc = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            ccc[i] = categoryList.get(i).getName();
        }
        List<Integer> selectData_a = new ArrayList<>();
        selectData_a.add(0);
        List<Integer> selectData_b = new ArrayList<>();
        selectData_b.add(0);
        //selectData_b.add(3);

        List<Integer> selectData_d = new ArrayList<>();
        selectData_d.add(0);
        final List<TabMenuModel> tabSelectModels = new ArrayList<>();//用来存放初始化选项的状态
        TabMenuModel tabMenuModel = new TabMenuModel("综合评价", aaa, selectData_a);
        tabMenuModel.setTabButtonView(new TButton(this));
        tabMenuModel.setColorContent(mContext.getResources().getColor(R.color.main_color),mContext.getResources().getColor(R.color.main_color_gray_46));
        tabSelectModels.add(tabMenuModel);
        TabMenuModel tabMenuModel1 = new TabMenuModel("全部", bbb, 1, selectData_b);
        tabMenuModel1.setTabButtonView(new TButton(this));
        tabMenuModel1.setColorContent(mContext.getResources().getColor(R.color.main_color),mContext.getResources().getColor(R.color.main_color_gray_46));
        tabSelectModels.add(tabMenuModel1);
        List<Integer> selectData_c = new ArrayList<>();
        int category_Index=1;
        for (int i=0;i<categoryList.size();i++){
            if(categoryList.get(i).getCode().equals(category)){
                category_Index =i;
                continue;
            }
        }
        selectData_c.add(category_Index);

        TabMenuModel tabMenuModel2 = new TabMenuModel("全科", ccc,  1,selectData_c);
        tabMenuModel2.setTabButtonView(new TButton(this));
        tabMenuModel2.setColorContent(mContext.getResources().getColor(R.color.main_color),mContext.getResources().getColor(R.color.main_color_gray_46));
        tabSelectModels.add(tabMenuModel2);

        //添加自定义布局
        TabMenuModel tabMenuModel3 =new TabMenuModel("时间", R.layout.layout_datetime_picker_menu, new TabMenuModel.OnCreatTabContentView() {
            @Override
            public void onCreat(View root) {
                showDateTimePick(root);
            }
        });
        tabMenuModel3.setTabButtonView(new TButton(this));
        tabSelectModels.add(tabMenuModel3);

        /*********************      组建初始化                   ************************/
        tabMenuLayout = findViewById(R.id.tab_menu_layout);
        tabMenuLayout.setTabDividerResId(R.layout.tab_layout_driver);
        tabMenuLayout.setData(tabSelectModels, new TabMenuLayout.TabMenuInterface() {
            @Override
            public String onSelected(TabRadioGroup.TabRadioButton tabButton,int tabIndex, int position) {
                //PopToastUtil.ShowToast((Activity) mContext, "" + tabIndex + ":" + position);
                switch (tabIndex){
                    case 0://评价
                        evaluationRankType = evaluationRankTypes[position];
                        tabButton.setTabName(aaa[position]);
                        break;
                    case 1://医生类型
                        title = titles[position];
                        tabButton.setTabName(bbb[position]);
                        break;
                    case 2://科室
                        category =categoryList.get(position).getCode();
                        tabButton.setTabName(ccc[position]);
                        break;
                    case 3://时间
                        //tabButton.setTabName("??");
                        break;
                }
                getData();
                getDataAll();
                tabMenuLayout.getPopupWindow().dismiss();
                return null;
            }

        });


        title =null;//初始化为空，才返回所有
       // requestDate = DateTimeUtil.getToday().getYearMonthDay();//默认时间
       // requestTime  = DateTimeUtil.getToday().getHourMinute();
        evaluationRankType = evaluationRankTypes[0];//默认综合评价
        getData();
        getDataAll();
    }

    private String dateTime_ydm;
    private String dateTime_hour;
    private String dateTime_minute;
    private void showDateTimePick(View contentView) {
        DateTimePickerPopView timePickerPopWin = new DateTimePickerPopView.Builder(mContext,contentView, new DateTimePickerPopView.OnTimePickListener() {
            @Override
            public void onTimePickCompleted(String ymd, String hour, String minute, String time) {
                //dateTime = hour + "-" + minute;
                dateTime_ydm = ymd;
                dateTime_hour = hour;
                dateTime_minute = minute;
                //UserHelper.getInstance().setDateTime(time);

                requestDate = dateTime_ydm;//改变时间
                requestTime  = dateTime_hour+":"+dateTime_minute;

                getData();
                getDataAll();
                tabMenuLayout.getPopupWindow().dismiss();
                //PopToastUtil.ShowToast(mContext, time);
            }
        }).textConfirm("确定")
                .textCancel("取消")
                .btnTextSize(16)
                .viewTextSize(25)
                .setDefaultPosition(dateTime_ydm, dateTime_hour, dateTime_minute)
                .colorCancel(getResources().getColor(R.color.main_color))
                .colorConfirm(getResources().getColor(R.color.main_color))
                .colorSignText(getResources().getColor(R.color.main_color))
                .colorContentText(Color.GRAY, getResources().getColor(R.color.main_color), Color.GRAY)
                .setSignText(getResources().getString(R.string.year), getResources().getString(R.string.month), getResources().getString(R.string.day))
                .build();
    }

    private List doctorsResult = new ArrayList();
    private List doctors = new ArrayList();
    private Map<String,String> categoryNameMap = new HashMap<>();
    private void init() {

        this.mBundle = this.getIntent().getExtras();
        category = mBundle.getString("category");

        String dateTime = UserHelper.getInstance().getDateTime();
        dateTime_ydm = dateTime.split(" ")[0];
        dateTime_hour = dateTime.split(" ")[1].split(":")[0];
        dateTime_minute = dateTime.split(" ")[1].split(":")[1];
        //UserHelper.getInstance().setDateTime(time);

        requestDate = dateTime.split(" ")[0];//改变时间
        requestTime = dateTime_hour+":"+dateTime_minute;

        for(DepartMentModel departMentModel: getDataByType()){
            categoryNameMap.put(departMentModel.getCode(),departMentModel.getName());
        }
        doctorAdapterResult = new DoctorAdapter(this, doctorsResult,categoryNameMap);
        doctorAdapter = new DoctorAdapter(this, doctors,categoryNameMap);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recyDoctor.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recyDoctor.setAdapter(doctorAdapter);
        //添加动画
        recyDoctor.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        recyDoctor.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        //设置布局管理器
        recy_doctor_result.setLayoutManager(layoutManager2);
        //设置为垂直布局，这也是默认的
        layoutManager2.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recy_doctor_result.setAdapter(doctorAdapterResult);
        //添加动画
        recy_doctor_result.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        recy_doctor_result.setItemAnimator(new DefaultItemAnimator());
    }

    private String category;
    private String title;
    private String requestDate;
    private String requestTime;
    private String evaluationRankType;
    private void getData() {
        /*LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("列表获取中").setCanTouch(false).create();
        loadingDialog.show();*/
        final QDActionDialog qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(cn.demomaster.huan.doctorbaselibrary.R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("获取中")
                .create();
        qdActionDialog.show();

        Map map2 = new HashMap();
        map2.put("category", category);
        map2.put("requestDate", requestDate);
        map2.put("requestTime", requestTime);
        map2.put("title", title);
        map2.put("evaluationRankType", evaluationRankType);//"profession"
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //getMatchedDoctorList/getAllDoctorsInSpecifiedDomain
        HttpUtils.getMatchedDoctorList(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            try {
                                //JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                                List doctors1 = JSON.parseArray(response.getData().toString(), DoctorModelApi.class);
                                if(doctors1!=null && doctors1.size()>0){
                                    ((TextView)findViewById(R.id.tv_search_result_title)).setText("搜索结果");
                                }else {
                                    ((TextView)findViewById(R.id.tv_search_result_title)).setText("暂无搜索结果");
                                }
                                doctorsResult.clear();
                                doctorsResult.addAll(doctors1);
                                doctorAdapterResult.notifyDataSetChanged();
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

    private void getDataAll() {
        /*LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("列表获取中").setCanTouch(false).create();
        loadingDialog.show();*/
        final QDActionDialog qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(cn.demomaster.huan.doctorbaselibrary.R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("获取中")
                .create();
        qdActionDialog.show();

        Map map2 = new HashMap();
        map2.put("category", category);
        //map2.put("requestDate", requestDate);
        //map2.put("requestTime", requestTime);
        map2.put("title", title);
        map2.put("evaluationRankType", evaluationRankType);//"profession"
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //getMatchedDoctorList/getAllDoctorsInSpecifiedDomain//getAllDoctorsInSpecifiedDomain
        HttpUtils.getAllDoctorsInSpecifiedDomain(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            try {
                                //JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                                List doctors1 = JSON.parseArray(response.getData().toString(), DoctorModelApi.class);
                                if(doctors1!=null && doctors1.size()>0){
                                    ((TextView)findViewById(R.id.tv_search_result_title2)).setText("为您推荐");
                                }else {
                                    ((TextView)findViewById(R.id.tv_search_result_title2)).setText("暂无推荐");
                                }
                                doctors.clear();
                                doctors.addAll(doctors1);
                                doctorAdapter.notifyDataSetChanged();
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

/*
    private void findDoctorByCategory() {
        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("列表获取中").setCanTouch(false).create();
        loadingDialog.show();

        this.mBundle = this.getIntent().getExtras();
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("token", SessionHelper.getToken());//p是病人端，d是医生端
        map2.put("uuid", SessionHelper.getUuid());
        map2.put("address", "上海市");
        map2.put("appointmentTime", "2018-11-23 10:00:00");
        map2.put("category", mBundle.getString("category"));
        map2.put("doctorId", null);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.getAllDoctorsInSpecifiedDomain(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            //JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            //String token = jsonObject.get("token").toString();

                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "列表获取失败：" + response.getMessage());
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
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        loadingDialog.dismiss();
                    }
                });
    }*/

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
