package cn.demomaster.huan.doctorbaselibrary.view.activity.doctor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.DateTimePointAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.DateTimeHelper;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.CalendarTimeItemModel;
import cn.demomaster.huan.doctorbaselibrary.model.TimePointModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.DoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.util.DateTimeUtil;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.AppointmentActivity;
import cn.demomaster.huan.doctorbaselibrary.view.widget.time.DayTimePickerPopWin;
import cn.demomaster.huan.doctorbaselibrary.view.widget.time.HourModel;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.ActionBarState;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.loading.LoadingCircleView;
import cn.demomaster.huan.quickdeveloplibrary.widget.RatingBar;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDActionDialog;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.*;

/**
 * 医生详情
 */
public class DoctorDetailActivity extends BaseActivity {

    ImageView ivDoctorHead;
    TextView tvDoctorName;
    TextView tvDoctorCategory;
    TextView tv_language;
    TextView tvDoctorLevel;
    RatingBar ratingBarEvaluate;
    LinearLayout llEvaluate;
    TextView tvCharacteristic;
    TableLayout TableLayout2;
    RecyclerView recyDate;
    TextView tvSeletedDatetime;
    TextView tvAllEvaluate;
    TextView tv_evaluate_count;
    TextView tvAppointment;
    private LinearLayoutManager linearLayoutManager;
    private List<CalendarTimeItemModel> lists;
    private DateTimePointAdapter adapter;
    public String TAG = "CGQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        ivDoctorHead = findViewById(R.id.iv_doctor_head);
        tvDoctorName = findViewById(R.id.tv_doctor_name);
        tvDoctorCategory = findViewById(R.id.tv_doctor_category);
        tvDoctorLevel = findViewById(R.id.tv_doctor_level);
        tv_language = findViewById(R.id.tv_language);
        ratingBarEvaluate = findViewById(R.id.ratingBar_evaluate);
        ratingBarEvaluate.setActivateCount(5);
        ratingBarEvaluate.setFloat(false);
        ratingBarEvaluate.setBackResourceId(R.mipmap.ic_seekbar_star_normal);
        ratingBarEvaluate.setFrontResourceId(R.mipmap.ic_seekbar_star_selected);
        ratingBarEvaluate.setUseCustomDrable(true);
        llEvaluate = findViewById(R.id.ll_evaluate);
        tvCharacteristic = findViewById(R.id.tv_characteristic);
        TableLayout2 = findViewById(R.id.TableLayout2);
        recyDate = findViewById(R.id.recy_date);
        tvSeletedDatetime = findViewById(R.id.tv_seleted_datetime);
        tvAllEvaluate = findViewById(R.id.tv_all_evaluate);
        tv_evaluate_count = findViewById(R.id.tv_evaluate_count);
        tvAppointment = findViewById(R.id.tv_appointment);
        tvSeletedDatetime.setText("请选择和医生匹配的预约时间");

        getActionBarLayoutOld().setTitle("专家详情");
        init();
    }

    @Override
    public void onVerifyTokenSuccess() {
        super.onVerifyTokenSuccess();
        getDoctorInfo();
    }

    private String doctorId;
    private DoctorModelApi doctorInfo;

    private void init() {
        getActionBarLayoutOld().getActionBarTip().setLoadingStateListener(new ActionBarState.OnLoadingStateListener() {
            @Override
            public void onLoading(ActionBarState.Loading loading) {
                getOnVerifyTokenResult().onRetry();
            }
        });

        tvAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doctorInfo == null) {
                    PopToastUtil.ShowToast(mContext, "错误：医生信息不完善");
                    return;
                }
                if (time == null) {
                    PopToastUtil.ShowToast(mContext, "请选择预约时间");
                    return;
                }

                Intent intent = new Intent(mContext,AppointmentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctor", doctorInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent,201);
            }
        });

        if (mBundle != null && mBundle.containsKey("doctorId")) {
            doctorId = mBundle.getString("doctorId");
        }
        if (doctorId == null) {
            return;
        }

        //模拟一些数据加载
        lists = DateTimeHelper.getDateTimeList();
        for(int i =0;i<lists.size();i++){
            if(lists.get(i).isToday()){
                todayIndex = i;
            }
        }
        //这里使用线性布局像listview那样展示列表,第二个参数可以改为 HORIZONTAL实现水平展示
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        //使用网格布局展示
        recyDate.setLayoutManager(new GridLayoutManager(this, 7));
        //recy_drag.setLayoutManager(linearLayoutManager);
        adapter = new DateTimePointAdapter(this, lists);
        //设置分割线使用的divider
        //recy_drag.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, android.support.v7.widget.DividerItemDecoration.VERTICAL));
        recyDate.setAdapter(adapter);
        String date = DateTimeUtil.getToday().getYearMonthDay();

        getTimeSchedule(date, lists.get(todayIndex + 13).getDate());

    }

    int todayIndex;

    //更新时间日历
    void updateDateTime() {
        for (int i = 0; i < lists.size(); i++) {
            CalendarTimeItemModel calendarTimeItemModel = lists.get(i);
            if (inArrayAndNutEmpty(calendarTimeItemModel.getDate())) {
                calendarTimeItemModel.setFree(true);
                calendarTimeItemModel.setAvailable(true);
            } else {
                calendarTimeItemModel.setFree(false);
                calendarTimeItemModel.setAvailable(false);
            }
            lists.set(i, calendarTimeItemModel);
        }
        adapter = new DateTimePointAdapter(this, lists);
        adapter.setOnItemClick(new DateTimePointAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                //可以调用时间组建
                showDateTimePick(position);
            }
        });
        recyDate.setAdapter(adapter);
    }

    private boolean inArray(String date) {
        for (TimePointModel pointModel : timePointModels) {
            if (pointModel.getScheduleDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    private boolean inArrayAndNutEmpty(String date) {
        for (TimePointModel pointModel : timePointModels) {
            if (pointModel.getScheduleDate().equals(date)) {
                if (TextUtils.isEmpty(pointModel.getAvailableTime())) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private String time;
    public static String format2LenStr(int num) {
        return num < 10 ? "0" + num : String.valueOf(num);
    }
    private int getDateIndex(String date) {
        for (int i = 0; i < timePointModels.size(); i++) {
            TimePointModel pointModel = timePointModels.get(i);
            if (pointModel.getScheduleDate().equals(date)) {
                return i;
            }
        }
        return 0;
    }

    //显示可预约的时间
    private void showDateTimePick(int position) {
        List<HourModel> hourModels = new ArrayList<>();
        position = getDateIndex(lists.get(position).getDate());
        if (position > timePointModels.size() - 1 || TextUtils.isEmpty(timePointModels.get(position).getAvailableTime())) {
            return;
        }
        String[] points = timePointModels.get(position).getAvailableTime().split(",");
        //日期   //小时  //分钟
        LinkedHashMap<String, LinkedHashMap<String, String>> hourMap = new LinkedHashMap<>();
        for (int i = 0; i < points.length; i++) {
            String key = points[i].substring(0, 2);
            if (hourMap.containsKey(key)) {
                LinkedHashMap<String, String> map = hourMap.get(key);
                String key2 = points[i].substring(3, 5);
                map.put(key2, key2);
                hourMap.put(key, map);
            } else {
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                String key2 = points[i].substring(3, 5);
                map.put(key2, key2);
                hourMap.put(key, map);
            }
        }
        for (Map.Entry entry : hourMap.entrySet()) {
            HourModel hourModel = new HourModel();
            hourModel.setDate(timePointModels.get(position).getScheduleDate());
            hourModel.setHour((String) entry.getKey());
            List<String> childlist = new ArrayList<>();
            for (Map.Entry entry2 : ((Map<String, String>) entry.getValue()).entrySet()) {
                childlist.add("" + entry2.getKey());
            }
            hourModel.setChild(childlist);
            hourModels.add(hourModel);
        }
        Log.i(TAG, "" + hourMap.size());
       /* for (int i = 0; i <points.length; i++) {
            HourModel hourModel = new HourModel();
            hourModel.setDate("2018-12-05");
            hourModel.setHour(points[i].substring(0,1));
            List<String> childlist = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                childlist.add("" + j * 15);
            }
            hourModel.setChild(childlist);
            hourModels.add(hourModel);
        }*/
        DayTimePickerPopWin timePickerPopWin = new DayTimePickerPopWin.Builder(mContext, hourModels, new DayTimePickerPopWin.OnTimePickListener() {
            @Override
            public void onTimePickCompleted(HourModel hourModel, String minute) {
                PopToastUtil.ShowToast(mContext, hourModel.getDate() + " " + minute);
                time = hourModel.getDate() + " " + format2LenStr(Integer.valueOf(hourModel.getHour())) + ":" + format2LenStr(Integer.valueOf(minute)) + ":00";
                UserHelper.getInstance().setDateTime(time);
                tvSeletedDatetime.setText("已选择预约时间：" + hourModel.getDate() + " " + format2LenStr(Integer.valueOf(hourModel.getHour())) + ":" + format2LenStr(Integer.valueOf(minute)) + ":00");
            }
        }).textConfirm("确定")
                .textCancel("取消")
                .btnTextSize(16)
                .viewTextSize(25)
                .setDefIndex(0, 0)
                .colorCancel(getResources().getColor(R.color.main_color))
                .colorConfirm(getResources().getColor(R.color.main_color))
                .colorSignText(getResources().getColor(R.color.main_color))
                .colorContentText(Color.GRAY, getResources().getColor(R.color.main_color), Color.GRAY)
                .setSignText(getResources().getString(R.string.year), getResources().getString(R.string.month), getResources().getString(R.string.day))
                .build();
        timePickerPopWin.showPopWin(mContext);
    }

    private void refreshUI() {
        if (doctorInfo == null) {
            return;
        }

        RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                .skipMemoryCache(false);//不做内存缓存

        Glide.with(mContext).load(doctorInfo.getPhotoUrl()).apply(mRequestOptions).into(ivDoctorHead);
        tvDoctorName.setText(doctorInfo.getDoctorName());
        tv_language.setText(doctorInfo.getLanguage());
        //tvDoctorCategory.setText(doctorInfo.getDomain());
        final String[] titles = { "主任医师", "副主任医师", "主治医师"};
        String title ="";
        switch (doctorInfo.getTitle()){
            case "1":
                title = titles[0];
                break;
            case "2":
                title = titles[1];
                break;
            case "3":
                title = titles[2];
                break;
        }
        tvDoctorLevel.setText(title);
        tvCharacteristic.setText(doctorInfo.getSpecialDomainDesc());
        ratingBarEvaluate.setColor(mContext.getResources().getColor(R.color.main_color), mContext.getResources().getColor(R.color.white));
        ratingBarEvaluate.setActivateCount(doctorInfo.getOverallMerit());
        llEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctorInfo", doctorInfo);
                startActivity(DoctorEvaluateActivity.class, bundle);
            }
        });
        tvAllEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctorInfo", doctorInfo);
                startActivity(DoctorEvaluateActivity.class, bundle);
            }
        });
        ratingBarEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctorInfo", doctorInfo);
                startActivity(DoctorEvaluateActivity.class, bundle);
            }
        });
        tv_evaluate_count.setText("患者评价("+doctorInfo.getEvaluationNum()+")");

    }
/*
    private List<CalendarTimeItemModel> getDateTime() {
        List<CalendarTimeItemModel> lists = new ArrayList<>();
        //获取当前日期
        String today = DateTimeUtil.StringData();
        //获取是星期几
        int weekIndex = DateTimeUtil.getWeek(today);
        int oldCount = weekIndex - 2;
        Date oldDate = DateTimeUtil.getOldDate(-oldCount);//获取前-n天的日期
        String week = "";
        List<String> dateList = DateTimeUtil.getDateAsCount(oldDate, 21);
        boolean isFree = false;
        boolean isAvailable = false;
        int availableCount = 0;
        int i = 0;
        for (String s : dateList) {
            CalendarTimeItemModel dateTimeModel = new CalendarTimeItemModel();
            System.out.println(s);
            if (s.equals(DateTimeUtil.StringData())) {
                //week = "今天";
                week = s.substring(s.length() - 5, s.length());
                week = week.replace("-", "月") + "日";
                todayIndex = i;
                isAvailable = true;
                isFree = true;
                dateTimeModel.setSpecial(true);//特殊日期需要标红
            } else {
                //每逢15或初一 ，显示为 YY-DD
                int day = Integer.valueOf(s.substring(s.length() - 2, s.length()));
                if (day == 1) {
                    week = s.substring(s.length() - 5, s.length());
                    week = week.replace("-", "月") + "日";
                    //dateTimeModel.setSpecial(true);//特殊日期需要标红
                } else {
                    week = "" + day;//"周"+DateTimeUtil.getWeek(s);
                }
            }
            if (isAvailable) {
                availableCount++;
                dateTimeModel.setAvailable(true);
                if (availableCount == 14) {
                    isAvailable = false;
                }
            }
            dateTimeModel.setTitle(week);
            dateTimeModel.setDate(s);
            dateTimeModel.setFree(isFree);
            lists.add(dateTimeModel);
            i = i + 1;
        }

        return lists;
    }*/


    private List<TimePointModel> timePointModels;

    private void getTimeSchedule(String startDate, String endDate) {
        /*LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("获取中").setCanTouch(false).create();
        loadingDialog.show();*/

        final QDActionDialog qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(cn.demomaster.huan.doctorbaselibrary.R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("获取中")
                .create();
        qdActionDialog.show();

        this.mBundle = this.getIntent().getExtras();
        /*String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);*/
        Map map2 = new HashMap();
        map2.put("doctorId", doctorId);
        map2.put("startDate", startDate);
        map2.put("endDate", endDate);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.getTimeSchedule(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            try {
                                timePointModels = JSON.parseArray(response.getData().toString(), TimePointModel.class);
                                updateDateTime();
                                //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                                // JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                                /*orders.clear();
                                List<OrderModelApi> doctors1 = JSON.parseArray(response.getData().toString(), OrderModelApi.class);
                                orders.addAll(doctors1);
                                orderAdapter.notifyDataSetChanged();
                                //String token = jsonObject.get("token").toString();*/
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            getActionBarLayoutOld().getActionBarTip().showError("失败：" + response.getMessage());
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

    /**
     * 医生信息获取
     */
    private void getDoctorInfo() {
        /*LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("医生信息获取中").setCanTouch(false).create();
        loadingDialog.show();*/
       /* final QDActionDialog qdActionDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(cn.demomaster.huan.doctorbaselibrary.R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("获取中")
                .create();
        qdActionDialog.show();*/

        this.mBundle = this.getIntent().getExtras();
        Map map2 = new HashMap();
        map2.put("doctorId", doctorId);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.getDoctorInfo(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            try {
                                doctorInfo = JSON.parseObject(response.getData().toString(), DoctorModelApi.class);
                                refreshUI();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            getActionBarLayoutOld().getActionBarTip().showError("失败：" + response.getMessage());
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
                        //qdActionDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        //qdActionDialog.dismiss();
                    }
                });
    }
}
