package cn.demomaster.huan.doctorbaselibrary.view.activity.order;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;

import cn.demomaster.huan.doctorbaselibrary.application.MyApp;
import cn.demomaster.huan.doctorbaselibrary.model.DepartMentModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.*;
import cn.demomaster.huan.doctorbaselibrary.view.activity.setting.CommonResultActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.simplepicture.model.Image;
import cn.demomaster.huan.quickdeveloplibrary.helper.simplepicture.view.SimplePictureGallery;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.loading.LoadingCircleView;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDActionDialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import com.alibaba.sdk.android.oss.*;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约页面
 */
public class AppointmentActivity extends BaseActivity {

    TextView tvPatientName;
    TextView tvAddress;
    TextView tvTime;
    TextView tvDoctorName;
    TextView tv_photo_tip;
    TextView tv_price_tip;
    TextView tv_department_name;
    RadioGroup rgDoctorType;
    EditText etDescription;
    ImageView iv_agree;
    TextView tvSubmit;
    CheckBox rb_doctor_type_01, rb_doctor_type_02, rb_doctor_type_03;
    LinearLayout llPanelDoctorName,ll_panel_department;
    private boolean hasDoctor;//是否是指定医生预约
    private boolean userAggre;//同意条款

    @Override
    public void onVerifyTokenSuccess() {
        super.onVerifyTokenSuccess();
        //客户端，提交预约请求页面，收费标准提醒
        getChargeTips();
        //验证登陆态有效，检查用户是否实名认证过了
        //requestCheck();
    }

    private List<ChargeTipModelApi> chargeTipModelApis = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    List<String> titles_stand = new ArrayList<>();

    /**
     * 获取收费提示
     */
    private void getChargeTips() {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        titles_stand.clear();
        titles_stand.add("1");
        titles_stand.add("2");
        titles_stand.add("3");
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("titles", titles_stand);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, "getChargeTips=" + map_str2);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        HttpUtils.getChargeTips(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            if (!TextUtils.isEmpty(response.getData().toString())) {
                                chargeTipModelApis = JSON.parseArray(response.getData().toString(), ChargeTipModelApi.class);
                            }
                            changeDoctorInfo();
                            changeTip();
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

    /**
     * 同步网络接口返回的数据
     */
    private void changeDoctorInfo() {
        if(doctorModelApi!=null) {
            String title = getChargeTipModelApi(doctorModelApi.getTitle()).getTitleName();
            Cursor cursor = MyApp.getInstance().db.query("inner_department_category", new String[]{"id", "categoryName", "categoryCode", "type", "iconName"}, "isValid=? and categoryCode=? ORDER BY \"index\" ASC", new String[]{"1", "" + doctorModelApi.getDomain()}, null, null, null);

            String departMentName = "";
            DepartMentModel departMentModel = null;
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("categoryName"));
                String code = cursor.getString(cursor.getColumnIndex("categoryCode"));
                //String type = cursor.getString(cursor.getColumnIndex("type"));
                String iconName = cursor.getString(cursor.getColumnIndex("iconName"));
                departMentModel = new DepartMentModel();
                departMentModel.setName(name);
                departMentModel.setIconName(iconName);
                departMentModel.setCode(code);
                departMentName = name;
                // Log.i(TAG, "query------->" + "categoryName：" + name + " " + ",categoryCode：" + code + ",type：" + type + ",iconName：" + iconName);
            }
            tvDoctorName.setText(doctorModelApi.getDoctorName() + "," + departMentName + "," + title);
        }else {
            category = mBundle.getString("departMentId");
            if (category == null) {
                return;
            }
            ll_panel_department.setVisibility(View.VISIBLE);
            Cursor cursor = MyApp.getInstance().db.query("inner_department_category", new String[]{"id", "categoryName", "categoryCode", "type", "iconName"}, "isValid=? and categoryCode=? ORDER BY \"index\" ASC", new String[]{"1", "" + category}, null, null, null);

            String departMentName = "";
            DepartMentModel departMentModel = null;
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("categoryName"));
                String code = cursor.getString(cursor.getColumnIndex("categoryCode"));
                //String type = cursor.getString(cursor.getColumnIndex("type"));
                String iconName = cursor.getString(cursor.getColumnIndex("iconName"));
                departMentModel = new DepartMentModel();
                departMentModel.setName(name);
                departMentModel.setIconName(iconName);
                departMentModel.setCode(code);
                departMentName = name;
                // Log.i(TAG, "query------->" + "categoryName：" + name + " " + ",categoryCode：" + code + ",type：" + type + ",iconName：" + iconName);
            }
            tv_department_name.setText(departMentName);
        }
        if (mBundle != null && mBundle.containsKey("doctor")) {
            titles.clear();
            titles.add(doctorModelApi.getTitle());
            changeTip();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        tvPatientName = findViewById(R.id.tv_patient_name);
        tvAddress = findViewById(R.id.tv_address);
        tvTime = findViewById(R.id.tv_time);
        tv_department_name = findViewById(R.id.tv_department_name);
        tv_photo_tip = findViewById(R.id.tv_photo_tip);
        tv_photo_tip.setText("请传照片，最多可上传十张");
        tvDoctorName = findViewById(R.id.tv_doctor_name);
        tv_price_tip = findViewById(R.id.tv_price_tip);
        rgDoctorType = findViewById(R.id.rg_doctor_type);
        etDescription = findViewById(R.id.et_description);
        iv_agree = findViewById(R.id.iv_agree);
        iv_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAggre = !userAggre;
                ((ImageView) v).setImageResource(userAggre ? R.mipmap.ic_checkbox_circle_select : R.mipmap.ic_checkbox_circle_normal);
            }
        });
        tvSubmit = findViewById(R.id.tv_submit);
        llPanelDoctorName = findViewById(R.id.ll_panel_doctor_name);
        ll_panel_department= findViewById(R.id.ll_panel_department);

        rb_doctor_type_01 = findViewById(R.id.rb_doctor_type_01);
        rb_doctor_type_02 = findViewById(R.id.rb_doctor_type_02);
        rb_doctor_type_03 = findViewById(R.id.rb_doctor_type_03);

        rb_doctor_type_01.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addOrRemove("1", isChecked);
            }
        });
        rb_doctor_type_02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addOrRemove("2", isChecked);
            }
        });
        rb_doctor_type_03.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addOrRemove("3", isChecked);
            }
        });
        //默认都选中
        titles.clear();
        titles.add("1");
        titles.add("2");
        titles.add("3");
        rb_doctor_type_01.setChecked(true);
        rb_doctor_type_02.setChecked(true);
        rb_doctor_type_03.setChecked(true);


        SimplePictureGallery simplePictureGallery = findViewById(R.id.ga_picture);
        simplePictureGallery.setMaxCount(10);
        simplePictureGallery.setSpanCount(4);
        simplePictureGallery.setCanPreview(true);
        simplePictureGallery.setAutoWidth(true);
        simplePictureGallery.setAddButtonPadding(50);
        simplePictureGallery.setItemMargin(5);
        simplePictureGallery.setOnPictureChangeListener(new SimplePictureGallery.OnPictureChangeListener() {
            @Override
            public void onChanged(List<Image> list) {
                imageList = list;
                if (list != null && list.size() > 0) {
                    tv_photo_tip.setText("已上传" + list.size() + "张照片，最多可上传十张");
                } else {
                    tv_photo_tip.setText("上传照片，最多可上传十张");
                }
            }
        });
        simplePictureGallery.getImages();
        getActionBarLayoutOld().setTitle("确认预约信息");

        init();
    }

    //添加或者删除收费标准
    private void addOrRemove(String s, boolean isChecked) {
        if (isChecked) {
            if (!titles.contains(s)) {
                titles.add(s);
            }
        } else {
            if (titles.contains(s)) {
                titles.remove(s);
            }
        }
        changeTip();
    }

    /**
     * 动态调整提示内容
     */
    private void changeTip() {
        if (chargeTipModelApis == null) {
            return;
        } else {
            tv_price_tip.setText("请先选择要预约的医生类型");
        }
        if (titles != null && titles.size() > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            String[] titles_t = {"1", "2", "3"};
            for (int i = 0; i < titles_t.length; i++) {
                String code = titles_t[i];
                if (titles.contains(code)) {
                    ChargeTipModelApi chargeTipModelApi = getChargeTipModelApi(code);
                    if (chargeTipModelApi != null) {
                        stringBuffer.append("“" + chargeTipModelApi.getTitleName() + "”" + chargeTipModelApi.getBaseFee() + "元/30分钟，超出部分" + chargeTipModelApi.getExtraFee() + "元/1分钟；\n\r");
                    }
                }
            }
            stringBuffer.append(" 每次服务时间约定为30分钟，不满30分钟按照30分钟计；超过30分钟后按照每1分钟额外计费，不满1分钟按照1分钟计；请勾选表示知悉。");
            tv_price_tip.setText(stringBuffer);
        } else {
            tv_price_tip.setText("请先选择要预约的医生类型");
        }
    }

    private ChargeTipModelApi getChargeTipModelApi(String code) {
        for (ChargeTipModelApi modelApi : chargeTipModelApis) {
            if (code.equals(modelApi.getTitleCode())) {
                return modelApi;
            }
        }
        return null;
    }

    private DoctorModelApi doctorModelApi;//指定的医生
    private String category;//按照专科代码选择
    private UserModelApi patient;
    private AddRessModel addRessModel;
    private String dateTime;
    private int doctorType = 1;

    private void init() {

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySubmint();
            }
        });

        mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey("doctor")) {
            hasDoctor = true;
            rgDoctorType.setVisibility(View.GONE);
            llPanelDoctorName.setVisibility(View.VISIBLE);
            ll_panel_department.setVisibility(View.GONE);

            doctorModelApi = (DoctorModelApi) mBundle.getSerializable("doctor");
            if (doctorModelApi == null) {
                return;
            }
            category = doctorModelApi.getDomain();
            if (category == null) {
                return;
            }

            tvDoctorName.setText(doctorModelApi.getDoctorName());
        } else if (mBundle != null && mBundle.containsKey("departMentId")) {
            hasDoctor = false;
            llPanelDoctorName.setVisibility(View.GONE);
            rgDoctorType.setVisibility(View.VISIBLE);


        } else {
            return;
        }
        patient = UserHelper.getInstance().getCurrentPatient();
        if (patient != null) {
            tvPatientName.setText(patient.getUserName());
        }
        addRessModel = UserHelper.getInstance().getAddRessModel();
        if (addRessModel != null) {
            tvAddress.setText(addRessModel.getAddressName());
        }
        dateTime = UserHelper.getInstance().getDateTime();
        if (dateTime != null) {
            tvTime.setText(dateTime);
        }

    }

    private void trySubmint() {
        if (patient == null) {
            PopToastUtil.ShowToast(mContext, "病人不能为空");
            return;
        }
        if (addRessModel == null) {
            PopToastUtil.ShowToast(mContext, "地址不能为空");
            return;
        }
        if (dateTime == null) {
            PopToastUtil.ShowToast(mContext, "日期时间不能为空");
            return;
        }

        if (!rb_doctor_type_01.isChecked() && !rb_doctor_type_02.isChecked() && !rb_doctor_type_03.isChecked()) {
            PopToastUtil.ShowToast(mContext, "请选择要预约的医生类型");
            return;
        }

        if (!userAggre) {
            PopToastUtil.ShowToast(mContext, "请勾选收费标准后操作");
            return;
        }
        if (hasDoctor) {
            category = doctorModelApi.getDomain();
        } else {
            if (rgDoctorType.getCheckedRadioButtonId() == R.id.rb_doctor_type_01) {
                doctorType = 1;
            }
            if (rgDoctorType.getCheckedRadioButtonId() == R.id.rb_doctor_type_02) {
                doctorType = 2;
            }
            if (rgDoctorType.getCheckedRadioButtonId() == R.id.rb_doctor_type_03) {
                doctorType = 3;
            }

        }
        /*LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        loadingDialog = builder.setMessage("正在提交").setCanTouch(false).create();
        loadingDialog.show();*/
        loadingDialog = new QDActionDialog.Builder(mContext)
                .setContentbackgroundColor(mContext.getResources()
                        .getColor(cn.demomaster.huan.doctorbaselibrary.R.color.transparent_dark_cc))
                .setBackgroundRadius(30)
                .setTopViewClass(LoadingCircleView.class)
                .setMessage("订单提交中")
                .create();
        loadingDialog.show();
        if (imageList != null && imageList.size() != 0) {//上传图片
            //校验通过，先获取oss口令
            getAssumeRole();
        } else {
            //提交
            findDoctorByCategory();
        }
    }

    private int currentIndex;//上传默认位置
    private List<Image> imageList;
    private Map<Integer, String> urlMap = new HashMap();
   // LoadingDialog loadingDialog;
    QDActionDialog loadingDialog;

    private void findDoctorByCategory() {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        //map2.put("token", SessionHelper.getToken());//p是病人端，d是医生端
        //map2.put("uuid", SessionHelper.getUuid());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("address", addRessModel.getAddressName());
        map2.put("appointmentTime", dateTime);
        map2.put("category", category);
        map2.put("doctorId", doctorModelApi == null ? null : doctorModelApi.getDoctorId());
        map2.put("patientId", patient.getUserId());
        map2.put("symptonDescByPatient", etDescription.getText() == null ? "" : etDescription.getText().toString());
        //map2.put("symptonPhotoUrl", etDescription.getText() == null ? "" : etDescription.getText().toString());

        StringBuilder imageUrl = new StringBuilder();
        for (int i = 0; i < urlMap.size(); i++) {
            if (i == urlMap.size() - 1) {
                imageUrl.append(urlMap.get(i) + "");
            } else {
                imageUrl.append(urlMap.get(i) + ";");
            }
        }
        map2.put("symptonPhotoUrl", imageUrl);
        String titles_str = "";
        for (int i = 0; i < titles.size(); i++) {
            if (i == 0) {
                titles_str = titles_str + titles.get(i);
            } else {
                titles_str = titles_str + "," + titles.get(i);
            }
        }
        map2.put("titles", titles_str);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.findDoctorByCategory(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            //String token = jsonObject.get("token").toString();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", "订单提交");
                            bundle.putString("message", "预约订单提交成功，请耐心等待～");
                            bundle.putString("button_message", "查看详情");
                            bundle.putInt("resultResId", R.mipmap.ic_result_success);
                            bundle.putBoolean("isOrder", true);
                            startActivity(CommonResultActivity.class, bundle);
                        } else if (response.getRetCode() == 3) {
                            showGideAuth();
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
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        loadingDialog.dismiss();
                    }
                });
    }


    public void getAssumeRole() {

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        String str_map_param = JSON.toJSONString(map2);
        Log.d(TAG, str_map_param);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), str_map_param);
        HttpUtils.getAssumeRole(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String securityToken = jsonObject.get("securityToken").toString();
                            String accessKeySecret = jsonObject.get("accessKeySecret").toString();
                            String accessKeyId = jsonObject.get("accessKeyId").toString();
                            initAliyunOss(accessKeyId, accessKeySecret, securityToken);
                            Log.i(TAG, "userdata=");
                        } else {
                            loadingDialog.dismiss();
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
                        loadingDialog.dismiss();
                        PopToastUtil.ShowToast((Activity) mContext, "出错了");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    private OSS oss;
    private String TAG = "CGQ";

    public void initAliyunOss(String accessKeyId, String secretKeyId, String securityToken) {
        Log.i(TAG, "初始化阿里云oss");
        //初始化阿里云oss
        //String endpoint = "http://idcard-front.oss-cn-hangzhou.aliyuncs.com";
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

        //if null , default will be init
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
        conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
        conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
        conf.setMaxErrorRetry(2); // retry，default 2
        OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv

        //OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider("LTAIEfRCQiMBiRjw", "MR7howkHIyEVTecoHudg1yfmjrcHqb", "123");
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, secretKeyId, securityToken);

        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider, conf);

        //初始化完成开始上传照片
        uploadFile();
    }


    public void uploadFile() {
        if (oss == null) {
            loadingDialog.dismiss();
            return;
        }
        String filePath = imageList.get(currentIndex).getPath();
        final String bucketName = "idcard-front";
        final String imageName = bucketName + "-" + SessionHelper.getUserName() + "-" + System.currentTimeMillis();
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(bucketName, imageName, filePath);

        // 异步上传时可以设置进度回调。
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);

            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                //公开的bucket
                //String url = oss.presignPublicObjectURL(bucketName,imageName);
                // Log.d("url=", url);
                String url = null;
                try {
                    //私有的的bucket
                    url = oss.presignConstrainedObjectURL(bucketName, imageName, 30 * 60);
                    //签名公开的访问URL
                    url = oss.presignPublicObjectURL(bucketName, imageName);
                    Log.d("url1=", url);
                } catch (ClientException e) {
                    loadingDialog.dismiss();
                    e.printStackTrace();
                }
                if (url != null) {
                    urlMap.put(currentIndex, url);
                    if (currentIndex != imageList.size() - 1) {//继续上传
                        currentIndex++;
                        uploadFile();
                    } else {//上传结束
                        currentIndex = 0;
                        findDoctorByCategory();
                    }
                } else {
                    currentIndex = 0;
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                loadingDialog.dismiss();
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
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
                            //showMessage(response.getMessage());
                            //PopToastUtil.ShowToast((Activity) mContext, "" + response.getMessage());
                            showGideAuth();
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

 /*   private PopWinSingleDialog popWinDialog_Auth;

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

}
