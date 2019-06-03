package cn.demomaster.huan.doctorbaselibrary.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.PatientHeaderAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.TimerSpan;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.UserModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.util.AppStateUtil;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity_NoActionBar;
import cn.demomaster.huan.doctorbaselibrary.view.activity.doctor.DepartmentListActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.doctor.DoctorSearchActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.login.LoginActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.EvaluateActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.OrderTimerActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.PayActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.setting.NotificationActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.user.UserInfoActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.user.address.AddressListActivity;
import cn.demomaster.huan.doctorbaselibrary.view.widget.time.DateTimePickerPopWin;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.OptionsMenu;
import cn.demomaster.huan.quickdeveloplibrary.helper.PhotoHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.operatguid.GuiderHelper;
import cn.demomaster.huan.quickdeveloplibrary.operatguid.GuiderModel;
import cn.demomaster.huan.quickdeveloplibrary.util.AnimationUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.DisplayUtil;
import cn.jpush.android.api.JPushInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;
import static cn.demomaster.huan.doctorbaselibrary.view.activity.order.OrderTimerActivity.OrderTimeTmpTag;

/**
 * 主页
 */
public class MainActivity extends BaseActivity_NoActionBar
        implements View.OnClickListener {

    TextView tvAddress;
    TextView tvTime;
    TextView llDoctor;
    TextView llDepartment;
    RelativeLayout relRoot;
    LinearLayout navView;
    //DrawerLayout drawerLayout;侧拉选项
    RecyclerView recyPatient;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_activity_main);

        tvAddress = findViewById(R.id.tv_address);
        tvAddress.setOnClickListener(this);
        tvTime = findViewById(R.id.tv_time);
        tvTime.setOnClickListener(this);
        llDoctor = findViewById(R.id.ll_doctor);
        llDoctor.setOnClickListener(this);
        llDepartment = findViewById(R.id.ll_department);
        llDepartment.setOnClickListener(this);
        relRoot = findViewById(R.id.rel_root);
        navView = findViewById(R.id.nav_view);
        //drawerLayout = findViewById(R.id.drawer_layout);
        recyPatient = findViewById(R.id.recy_patient);
        final EditText et_testid = findViewById(R.id.et_testid);
        et_testid.setVisibility(View.GONE);
       /* findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_testid.getText())) {
                    PopToastUtil.ShowToast(mContext, "输入订单id");
                }
                Bundle bundle = new Bundle();
                bundle.putString("doctorId", et_testid.getText().toString());
                bundle.putInt("activiType", 1);
                bundle.putString("targetClassName", EvaluateActivity.class.getName());

                startActivity(OrderTimerActivity.class, bundle);
            }
        });*/
        findViewById(R.id.btn_test).setVisibility(View.GONE);
        findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PayActivity.class);
            }
        });
        findViewById(R.id.btn_pay).setVisibility(View.GONE);

        AnimationUtil.addScaleAnimition(llDoctor, null);
        AnimationUtil.addScaleAnimition(llDepartment, null);
        initActivity();
        setFullScreen(false);
        //getActionBarLayout().setTitle("预约咨询");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppStateUtil.getInstance().isFirstOpen()) {//第一次引导
            initGuider();
            AppStateUtil.getInstance().setIsFirstOpen(false);
        }
    }

    /**
     * 初始化引导
     */
    private void initGuider() {
        String tips[] = {"更多功能这里", "查看消息点这里", "扫描医生二维码开始咨询", "在这里选择或添加用户", "选择/编辑上门地址","选择预约时间","专家预约——指定你想要的专家","专科预约——系统为您推荐匹配的专家"};

        //更多功能这里
        GuiderModel guiderModel_menu = new GuiderModel();
        guiderModel_menu.setLineType(GuiderModel.LINETYPE.straight);
        guiderModel_menu.setTargetView(new WeakReference<View>(actionBarInterface.getLeftView()));
        guiderModel_menu.setMessage(tips[0]);
        guiderModel_menu.setComplateType(GuiderModel.GuidActionType.CLICK);
        guiderModel_menu.setLineWidth(3);
        GuiderHelper.getInstance().setGuiderModel(guiderModel_menu);
        GuiderHelper.getInstance().add(guiderModel_menu);

        //查看消息点这里
        GuiderModel guiderModel_message = new GuiderModel();
        guiderModel_message.setLineType(GuiderModel.LINETYPE.straight);
        ImageView imageView = actionBarInterface.getContentView().findViewById(R.id.iv_actionbar_common_notification);
        guiderModel_message.setTargetView(new WeakReference<View>(imageView));
        guiderModel_message.setMessage(tips[1]);
        guiderModel_message.setComplateType(GuiderModel.GuidActionType.CLICK);
        guiderModel_message.setLineWidth(3);
        GuiderHelper.getInstance().add(guiderModel_message);

        //扫描医生二维码开始咨询
        GuiderModel guiderModel_scan = new GuiderModel();
        guiderModel_scan.setLineType(GuiderModel.LINETYPE.straight);
        guiderModel_scan.setTargetView(new WeakReference<View>(actionBarInterface.getRightView()));
        guiderModel_scan.setMessage(tips[2]);
        guiderModel_scan.setComplateType(GuiderModel.GuidActionType.CLICK);
        GuiderHelper.getInstance().add(guiderModel_scan);

        //在这里选择或添加用户
        GuiderModel guiderModel_add_patient = new GuiderModel();
        guiderModel_add_patient.setLineType(GuiderModel.LINETYPE.straight);
        guiderModel_add_patient.setTargetView(new WeakReference<View>(recyPatient));
        guiderModel_add_patient.setMessage(tips[3]);
        guiderModel_add_patient.setComplateType(GuiderModel.GuidActionType.CLICK);
        GuiderHelper.getInstance().add(guiderModel_add_patient);

        //在这里选择或添加地址
        GuiderModel guiderModel_add_address = new GuiderModel();
        guiderModel_add_address.setLineType(GuiderModel.LINETYPE.straight);
        guiderModel_add_address.setTargetView(new WeakReference<View>(findViewById(R.id.ll_address)));
        guiderModel_add_address.setMessage(tips[4]);
        guiderModel_add_address.setComplateType(GuiderModel.GuidActionType.CLICK);
        GuiderHelper.getInstance().add(guiderModel_add_address);

        //在这里选择时间
        GuiderModel guiderModel_select_timer = new GuiderModel();
        guiderModel_select_timer.setLineType(GuiderModel.LINETYPE.straight);
        guiderModel_select_timer.setTargetView(new WeakReference<View>(findViewById(R.id.ll_timmer)));
        guiderModel_select_timer.setMessage(tips[5]);
        guiderModel_select_timer.setComplateType(GuiderModel.GuidActionType.CLICK);
        GuiderHelper.getInstance().add(guiderModel_select_timer);

        //在这里选择时间
        GuiderModel guiderModel_select_doctor = new GuiderModel();
        guiderModel_select_doctor.setLineType(GuiderModel.LINETYPE.straight);
        guiderModel_select_doctor.setTargetView(new WeakReference<View>(findViewById(R.id.ll_doctor)));
        guiderModel_select_doctor.setMessage(tips[6]);
        guiderModel_select_doctor.setComplateType(GuiderModel.GuidActionType.CLICK);
        GuiderHelper.getInstance().add(guiderModel_select_doctor);

        //在这里选择时间
        GuiderModel guiderModel_select_department = new GuiderModel();
        guiderModel_select_department.setLineType(GuiderModel.LINETYPE.straight);
        guiderModel_select_department.setTargetView(new WeakReference<View>(findViewById(R.id.ll_department)));
        guiderModel_select_department.setMessage(tips[7]);
        guiderModel_select_department.setComplateType(GuiderModel.GuidActionType.CLICK);
        GuiderHelper.getInstance().add(guiderModel_select_department);

        GuiderHelper.getInstance().setBackgroundColor(0xaa000000);
        GuiderHelper.getInstance().setLineColor(Color.WHITE);
        GuiderHelper.getInstance().setTextColor(Color.WHITE);
        GuiderHelper.getInstance().setLineWidth(3);
        GuiderHelper.getInstance().setTextSize(48);
        GuiderHelper.getInstance().startGuider(mContext,guiderModel_menu.getTargetView().get(),"");
    }

    //private ImageView iv_user_header;
   // private List<MainMenu> menus = new ArrayList<>();

    /**
     * 初始化侧栏
     */
   /* private void initSliderMenu() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        RecyclerView recy_main_menu = drawer.findViewById(R.id.tv_main_menu);
        recy_main_menu.setAdapter(adapter_patient);
        toggle.syncState();

        iv_user_header = drawer.findViewById(R.id.iv_user_header);
        iv_user_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("patientId", UserHelper.getInstance().getCurrentPatient().getUserId());
                startActivity(PatientInfoActivity.class, bundle);
            }
        });
        AnimationUtil.addScaleAnimition(iv_user_header, null);
        TextView tv_userName = drawer.findViewById(R.id.tv_userName);
        tv_userName.setText(SessionHelper.getUserName());

        int[] menu_icons = {R.mipmap.ic_menu_expert, R.mipmap.ic_menu_order, R.mipmap.ic_menu_user_info,  R.mipmap.ic_menu_friend, R.mipmap.ic_menu_feedback, R.mipmap.ic_menu_help, R.mipmap.ic_menu_quit};// R.mipmap.ic_menu_setting,R.mipmap.ic_menu_pay,
        int[] menu_titles = {R.string.menu_expert, R.string.menu_order, R.string.menu_user_info, R.string.menu_friend, R.string.menu_feedback, R.string.menu_help, R.string.menu_quit};//R.string.menu_setting,// R.string.menu_pay,
        Class[] menu_clazzs = {null, OrderListActivity.class, PatientListActivity.class, ShareActivity.class, SaleServiceActivity.class, HelpCenterActivity.class, HelpCenterActivity.class, HelpCenterActivity.class, HelpCenterActivity.class, LoginActivity.class};//HelpCenterActivity.class,

        for (int i = 0; i < menu_icons.length; i++) {
            MainMenu mainMenu = new MainMenu(menu_icons[i], menu_titles[i], menu_clazzs[i]);
            menus.add(mainMenu);
        }
        MainMenuAdapter menuAdapter = new MainMenuAdapter(mContext, menus);
        menuAdapter.setOnItemClickedListener(new MainMenuAdapter.OnItemClickedListener() {
            @Override
            public void onClick(int position) {
                if (position == menus.size() - 1) {
                    exitApplication();
                } else {
                    if (menus.get(position).getTargetClszz() != null) {
                        ((BaseActivity_NoActionBar) mContext).startActivity(menus.get(position).getTargetClszz());
                    }
                    //PopToastUtil.ShowToast((Activity) mContext, mContext.getResources().getText(menus.get(position).getTitleResId()) + "正在开发...");
                }
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recy_main_menu.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        recy_main_menu.setAdapter(menuAdapter);
        //设置分隔线
        //recy_main_menu.addItemDecoration(new DividerGridItemDecoration(this));
        //设置增加或删除条目的动画
        recy_main_menu.setItemAnimator(new DefaultItemAnimator());
    }*/

    private RecyclerView recy_patient;
    private List<UserModelApi> patients;
    private PatientHeaderAdapter adapter_patient;
    private LinearLayoutManager linearLayoutManager;

    /**
     * 初始化用户头像列表
     */
    private void initPatientList() {
        recy_patient = findViewById(R.id.recy_patient);
        //ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new CallBack());
        //mItemTouchHelper.attachToRecyclerView(recy_drag);
        patients = new ArrayList<>();
        //这里使用线性布局像listview那样展示列表,第二个参数可以改为 HORIZONTAL实现水平展示
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        //使用网格布局展示
        //recy_patient.setLayoutManager(new GridLayoutManager(this, 5));
        linearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);//设置滚动方向，横向滚动
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recy_patient.setLayoutManager(linearLayoutManager);
        adapter_patient = new PatientHeaderAdapter(this, patients, 0);
        adapter_patient.setOnPatientChangedListener(new PatientHeaderAdapter.OnPatientChangedListener() {
            @Override
            public void onPatientChanged(UserModelApi patient) {
                UserHelper.getInstance().setCurrentPatient(patient);
                current_patient_id = patient.getUserId();
                PopToastUtil.ShowToast(mContext, "已选择" + patient.getUserName());
            }
        });
        //设置分割线使用的divider
        //recy_patient.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, android.support.v7.widget.DividerItemDecoration.VERTICAL));
        recy_patient.setAdapter(adapter_patient);
    }

    private String current_patient_id;
    private AddRessModel current_address;
    private String current_time;

    @Override
    public void onVerifyTokenSuccess() {
        super.onVerifyTokenSuccess();
        /*//验证登陆态有效，检查用户是否实名认证过了
        requestCheck();*/
        getPatientList();
        getOrderState();//获取订单状态，判断是否有订单正在计时
        initJiGuang();
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void initJiGuang(){
        JPushInterface.init(getApplicationContext());
        registerMessageReceiver();  // used for receive msg
    }

    private void initActivity() {
        //第一次打开直接进入引导页
       /* if (AppStateUtil.getInstance().isFirstOpen()) {
            startActivity(new Intent(MainActivity.this, GuideActivity.class));
            finish();
        } else */
       /* startActivity(AuthorizationActivity.class);
        finish();*/

        if (!AppStateUtil.getInstance().isLogined()) {//退出登录后会进入此逻辑
            startActivity(new Intent(mContext, LoginActivity.class));
            MainActivity.this.finish();
        } else {
            initHeader();
            //initSliderMenu();
            initPatientList();

            tvAddress.setOnClickListener(this);
            tvTime.setOnClickListener(this);
            llDepartment.setOnClickListener(this);
            llDoctor.setOnClickListener(this);
        }
    }

    /**
     * 初始化头部导航
     */
    private void initHeader() {
        actionBarInterface.show();
        actionBarInterface.getLeftView().setImageResource(R.mipmap.ic_menu);
        ImageView imageView = actionBarInterface.getContentView().findViewById(R.id.iv_actionbar_common_notification);
        imageView.setImageResource(R.mipmap.ic_notification);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(NotificationActivity.class);
            }
        });
        AnimationUtil.addScaleAnimition(imageView, null);
        actionBarInterface.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //drawer.openDrawer(Gravity.START);
                        ((BaseActivity_NoActionBar) mContext).startActivity(UserInfoActivity.class);
            }
        });
        actionBarInterface.getRightView().setImageResource(R.mipmap.ic_menu_add);
        actionBarInterface.setTitle("预约咨询");
        actionBarInterface.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOptionsMenu().show();

            }
        });
        initOptionsMenu();
    }

    OptionsMenu optionsMenu;
    /**
     * 初始化右上角menu
     */
    private void initOptionsMenu() {
        List<OptionsMenu.Menu> menus = new ArrayList<>();
        String[] menuNames = {"我的二维码","扫一扫"};//, "扫描"
        int[] resids = {R.mipmap.ic_menu_left_barcode, R.mipmap.ic_menu_left_scan};
        for (int i = 0; i < menuNames.length; i++) {
            OptionsMenu.Menu menu = new OptionsMenu.Menu();
            menu.setTitle(menuNames[i]);
            menu.setPosition(i);
            menu.setIconId(resids[i]);
            menu.setIconPadding(DisplayUtil.dip2px(mContext,8));
            menus.add(menu);
        }

        getOptionsMenuBuilder().setMenus(menus)
                .setAlpha(.6f)
                .setMargin(2)
                .setUsePadding(false)
                .setBackgroundColor(getResources().getColor(R.color.main_color))
                .setBackgroundRadius(15)
                .setWithArrow(true)
                .setArrowWidth(DisplayUtil.dip2px(mContext,12))
                .setArrowHeight(DisplayUtil.dip2px(mContext,12))
                .setTextColor(Color.WHITE)
                .setTextSize(16)
                .setDividerColor(Color.TRANSPARENT)
                .setAnchor(actionBarInterface.getRightView());
        getOptionsMenuBuilder().setOnMenuItemClicked(new OptionsMenu.OnMenuItemClicked() {
            @Override
            public void onItemClick(int position, View view) {
                switch (position) {
                    case 0:
                        /*if (doctorModelApi == null) {
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("doctor", doctorModelApi);
                        startActivity(DoctorCodeActivity.class, bundle);*/
                        optionsMenu.dismiss();
                        break;
                    case 1:
                        //二维码
                        photoHelper.scanQrcode(new PhotoHelper.OnTakePhotoResult() {
                            @Override
                            public void onSuccess(Intent data, String path) {
                                if (data != null) {
                                    //PopToastUtil.ShowToast(mContext, "扫描结果为：" + path);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("doctorId", path);
                                    bundle.putInt("activiType", 1);
                                    startActivity(OrderTimerActivity.class, bundle);
                                }
                            }

                            @Override
                            public void onFailure(String error) {

                            }
                        });
                        optionsMenu.dismiss();
                        break;
                }
            }
        });
        optionsMenu = getOptionsMenuBuilder().creat();
        actionBarInterface.getRightView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsMenu.show(v);
            }
        });
    }

    @Override
    public void onBackPressed() {
       /* if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_address) {
            startActivity(AddressListActivity.class);
        } else if (id == R.id.tv_time) {
            showDateTimePick();
        } else if (id == R.id.ll_doctor) {
            boolean a = checkUserInfo();
            if (!a) {
                return;
            }
            startActivity(DoctorSearchActivity.class);
        } else if (id == R.id.ll_department) {
            boolean b = checkUserInfo();
            if (!b) {
                return;
            }
            startActivity(DepartmentListActivity.class);
        }
    }

    /**
     * 检查用户信息是否完整
     *
     * @return
     */
    private boolean checkUserInfo() {
        if (TextUtils.isEmpty(current_patient_id)) {
            PopToastUtil.ShowToast(mContext, "选择病人");
            return false;
        }
        if (current_address == null) {
            PopToastUtil.ShowToast(mContext, "请选择地址");
            return false;
        }
        if (TextUtils.isEmpty(tvTime.getText())) {
            PopToastUtil.ShowToast(mContext, "请选择预约日期");
            return false;
        }
        current_time = tvTime.getText().toString();
        return true;
    }

    private String dateTime_ydm;
    private String dateTime_hour;
    private String dateTime_minute;

    /**
     * 显示时间选择器
     */
    private void showDateTimePick() {
        DateTimePickerPopWin timePickerPopWin = new DateTimePickerPopWin.Builder(mContext, new DateTimePickerPopWin.OnTimePickListener() {
            @Override
            public void onTimePickCompleted(String ymd, String hour, String minute, String time) {
                //dateTime = hour + "-" + minute;
                dateTime_ydm = ymd;
                dateTime_hour = hour;
                dateTime_minute = minute;
                tvTime.setText(time);
                UserHelper.getInstance().setDateTime(time);
                //PopToastUtil.ShowToast(mContext, time);
            }
        }).textConfirm("确定")
                .textCancel("取消")
                .btnTextSize(16)
                .viewTextSize(22)
                .setDefaultPosition(dateTime_ydm, dateTime_hour, dateTime_minute)
                .colorCancel(getResources().getColor(R.color.main_color))
                .colorConfirm(getResources().getColor(R.color.main_color))
                .colorSignText(getResources().getColor(R.color.main_color))
                .colorContentText(Color.GRAY, getResources().getColor(R.color.main_color), Color.GRAY)
                .setSignText(getResources().getString(R.string.year), getResources().getString(R.string.month), getResources().getString(R.string.day))
                .build();
        timePickerPopWin.showPopWin(mContext);
    }

    private UserModelApi getMainUser(List<UserModelApi> userModelApiList) {
        for (UserModelApi userModelApi : userModelApiList) {
            if (userModelApi.getPrimary() != null && userModelApi.getPrimary().equals("Y")) {
                return userModelApi;
            }
        }
        return null;
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
                            patients.clear();
                            patients.addAll(userModelApiList);
                            adapter_patient.notifyDataSetChanged();

                            UserHelper.getInstance().setCurrentPatient(patients.get(0));
                            current_patient_id = patients.get(0).getUserId();
                            UserModelApi userModelApi = getMainUser(userModelApiList);
                            UserHelper.getInstance().setPrimaryPatient(userModelApi);
                            getDefaultAddress();
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

    //检查是否需要认证
    private void requestCheck() {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.requestCheck(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {

                        } else {
                            //PopToastUtil.ShowToast((Activity) mContext, "" + response.getMessage());
                            showGideAuth();
                        }
                        getPatientList();
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });

    }

   /* private PopWinSingleDialog popWinDialog_Auth;

    //引导认证
    private void showGideAuth() {
        popWinDialog_Auth = PopWinSingleDialog.getInstance(mContext);
        popWinDialog_Auth.setContentText("为确保用户身份真实性，请先完成实人认证");
        popWinDialog_Auth.setBtn_text("开始");
        //, "为确保用户身份真实性，请先完成实人认证", , false);
        popWinDialog_Auth.setOnClickListener_ok(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWinDialog_Auth.dismiss();
                Class clazz = AppConfig.getInstance().getAuthorizationActivity();
                Intent intent = new Intent(mContext, clazz);
                startActivityForResult(intent, 111);
            }
        });
        popWinDialog_Auth.show();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
      获取默认地址
     */
    void getDefaultAddress() {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.getDefaultAddress(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            if (response.getData() != null && !response.getData().equals("null")) {
                                current_address = JSON.parseObject(response.getData().toString(), AddRessModel.class);
                                tvAddress.setText(current_address.getAddressName());
                                UserHelper.getInstance().setAddRessModel(current_address);
                            } else {
                                current_address = null;
                                tvAddress.setText("");
                            }
                        } else {
                            current_address = null;
                            tvAddress.setText("");
                            PopToastUtil.ShowToast((Activity) mContext, "地址获取失败：" + response.getMessage());
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
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    public void getOrderState() {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        retrofitInterface.getTrxInDiagnosing_Patient(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            try {
                                JSONArray jsonArray = JSON.parseArray(response.getData().toString());
                                if (jsonArray != null && jsonArray.size() > 0) {//有订单状态进行中跳转到计时页
                                    String trxId = JSON.parseObject(jsonArray.get(0).toString()).get("trxId").toString();
                                    String requestName = JSON.parseObject(jsonArray.get(0).toString()).get("requestName").toString();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("trxId", trxId);
                                    bundle.putString("requestName", requestName);
                                    bundle.putInt("activiType", 2);
                                    List<TimerSpan> timerSpanList = JSON.parseArray(response.getData().toString(), TimerSpan.class);
                                    TimerSpan timerSpan = null;
                                    if (timerSpanList != null && timerSpanList.size() > 0) {
                                        timerSpan = timerSpanList.get(0);
                                        bundle.putSerializable("TimerSpan", timerSpan);
                                    }
                                    startActivity(OrderTimerActivity.class, bundle);
                                } else {
                                    SharedPreferencesHelper.getInstance().setLong(OrderTimeTmpTag, -1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "fail：" + response.getMessage());
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
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    //if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    // }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e){
            }
        }
    }

    private void setCostomMsg(String msg){
        /*if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(android.view.View.VISIBLE);
        }*/
    }

}
