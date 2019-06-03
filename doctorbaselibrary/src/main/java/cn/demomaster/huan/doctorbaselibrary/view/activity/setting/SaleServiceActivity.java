package cn.demomaster.huan.doctorbaselibrary.view.activity.setting;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderModel2Api;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.OrderListActivity2;
import cn.demomaster.huan.quickdeveloplibrary.helper.simplepicture.model.Image;
import cn.demomaster.huan.quickdeveloplibrary.helper.simplepicture.view.SimplePictureGallery;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.StringVerifyUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.loader.LoadingDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;

/**
 * 留言客服
 */
public class SaleServiceActivity extends BaseActivity {

    private TextView tv_order_id, tv_doctor_info, tv_order_start_time, tv_patient_name;
    private EditText et_telephone, et_username;
    private EditText et_description;
    private Button btn_submit;
    private RadioGroup radioGroup_type;
    private SimplePictureGallery ga_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_service);

        radioGroup_type = findViewById(R.id.radioGroup_type);
        switchType();
        radioGroup_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isSystem = (checkedId == R.id.rb_01);
                switchType();
            }
        });

        tv_order_id = findViewById(R.id.tv_order_id);
        tv_order_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrderListActivity2.class);
                startActivityForResult(intent, 100);
            }
        });

        tv_patient_name = findViewById(R.id.tv_patient_name);
        tv_order_start_time = findViewById(R.id.tv_order_start_time);
        tv_doctor_info = findViewById(R.id.tv_doctor_info);
        et_telephone = findViewById(R.id.et_telephone);
        et_description = findViewById(R.id.et_description);
        et_username = findViewById(R.id.et_username);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToReport();
            }
        });

        ga_picture = findViewById(R.id.ga_picture);
        ga_picture.setAutoWidth(true);
        ga_picture.setAddButtonPadding(50);
        ga_picture.setItemMargin(5);
        ga_picture.setOnPictureChangeListener(new SimplePictureGallery.OnPictureChangeListener() {
            @Override
            public void onChanged(List<Image> images) {
                imageList = images;
            }
        });
        boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
        if(isPatient){
            findViewById(R.id.ll_panel_doctor_name).setVisibility(View.VISIBLE);
        }
    }

    private boolean hasOrderInfo = false;
    private boolean isSystem = true;

    private void switchType() {
        if (isSystem) {//系统问题
            findViewById(R.id.ll_orderInfo).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ll_orderInfo).setVisibility(View.VISIBLE);
            if (!hasOrderInfo) {
                findViewById(R.id.ll_orderInfo_detail).setVisibility(View.GONE);
            } else {
                findViewById(R.id.ll_orderInfo_detail).setVisibility(View.VISIBLE);
            }
        }
    }

    private void tryToReport() {
        if (!isSystem && !hasOrderInfo) {
            PopToastUtil.ShowToast(mContext, "请选择订单");
            return;
        }
        if (TextUtils.isEmpty(et_telephone.getText())) {
            PopToastUtil.ShowToast(mContext, "请填写手机号");
            return;
        }
        if (!checkTelePhone(et_telephone.getText().toString())) {
            PopToastUtil.ShowToast(mContext, "请填写正确的手机号");
            return;
        }
        if (TextUtils.isEmpty(et_description.getText())) {
            PopToastUtil.ShowToast(mContext, "请填写留言信息");
            return;
        }

        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        loadingDialog = builder.setMessage("正在提交").setCanTouch(false).create();
        loadingDialog.show();
        if (imageList != null && imageList.size() != 0) {
            //校验通过，先获取oss口令
            getAssumeRole();
        } else {
            submitReport();
        }
    }

    private int currentIndex;//上传默认位置
    private List<Image> imageList;
    private Map<Integer, String> urlMap = new HashMap();

    public boolean checkTelePhone(String username) {
        if (!StringVerifyUtil.validateMobilePhone(username)) {
            String str = getResources().getString(R.string.Cell_phone_format_error);
            et_telephone.setError(str);
            PopToastUtil.ShowToast((Activity) mContext, str);
            return false;
        }
        return true;
    }

    @Override
    public void onVerifyTokenSuccess() {
        super.onVerifyTokenSuccess();
        getDefaultInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == 100 && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle.containsKey("orderModelApi")) {
                OrderModel2Api orderModelApi = (OrderModel2Api) bundle.getSerializable("orderModelApi");
                if (orderModelApi != null) {
                    hasOrderInfo = true;
                    tv_order_id.setText(orderModelApi.getTrxName());

                    boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
                    if (isPatient) {//用户端 显示医生信息
                        //TODO 填充医生信息
                        if (TextUtils.isEmpty(orderModelApi.getDoctorName())) {
                            tv_doctor_info.setVisibility(View.GONE);
                        } else {
                            // final String[] bbb = {"全部", "主任医师", "副主任医师", "主治医师"};
                            String title = "";
                            int t = Integer.valueOf(orderModelApi.getTitle());
                            switch (t) {
                                case 1:
                                    title = "主任医师";
                                    break;
                                case 2:
                                    title = "副主任医师";
                                    break;
                                case 3:
                                    title = "主治医师";
                                    break;
                            }
                            tv_doctor_info.setText(orderModelApi.getDoctorName() + "  " + title);
                            tv_doctor_info.setVisibility(View.VISIBLE);
                        }
                    } else {//医生端不显示医生
                        tv_doctor_info.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(orderModelApi.getPatientName())) {
                        tv_patient_name.setText(orderModelApi.getPatientName());
                        tv_patient_name.setVisibility(View.VISIBLE);
                    }
                    tv_order_start_time.setText(orderModelApi.getAppointTime());
                    switchType();
                }
            }
        }
    }

    /**
     * 获取主注册用户的信息
     */
    private void getDefaultInfo() {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, "getChargeTips=" + map_str2);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        HttpUtils.getDefaultInfo(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String name = jsonObject.get("name").toString();
                            String phoneNum = jsonObject.get("phoneNumber").toString();
                            et_username.setText(name);
                            et_telephone.setText(phoneNum);
                            et_description.findFocus();
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


    //提交留言
    private void submitReport() {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("name", tv_order_id.getText().toString());
        StringBuilder imageUrl = new StringBuilder();
        for (int i = 0; i < urlMap.size(); i++) {
            if (i == urlMap.size() - 1) {
                imageUrl.append(urlMap.get(i) + "");
            } else {
                imageUrl.append(urlMap.get(i) + ";");
            }
        }
        map2.put("photoUrls", imageUrl);
        map2.put("message", et_description.getText().toString());
        map2.put("phoneNum", et_telephone.getText().toString());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, "getChargeTips=" + map_str2);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        HttpUtils.recordNewMessage(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            Bundle bundle = new Bundle();
                            bundle.putString("title", "留言提交");
                            bundle.putString("message", "您的留言已提交，我们会尽快与您联系～");
                            bundle.putString("button_message", "知道了");
                            bundle.putInt("resultResId", R.mipmap.ic_result_success);
                            startActivity(CommonResultActivity.class, bundle);
                            finish();
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
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        loadingDialog.dismiss();
                    }
                });
    }

    LoadingDialog loadingDialog;

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
                        submitReport();
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

}
