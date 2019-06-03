package cn.demomaster.huan.doctorbaselib.authorization;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.*;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.*;
import com.alibaba.security.rp.RPSDK;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.demomaster.huan.doctorbaselib.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.quickdeveloplibrary.camera.idcard.FileUtil;
import cn.demomaster.huan.quickdeveloplibrary.helper.PhotoHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.loader.LoadingDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AuthorizationActivity extends BaseActivity {


    Button btnCreatBucket;
    Button btnUploadFile;
    Button btnGetToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        initView();
        RPSDK.initialize(this);
        initAliyunOss();
    }

    private String filePath;

    private void initView() {
        btnCreatBucket = findViewById(R.id.btn_creat_bucket);
        btnUploadFile = findViewById(R.id.btn_upload_file);
        btnGetToken = findViewById(R.id.btn_get_token);
        btnCreatBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatBucket();
            }
        });

        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                photoHelper.takePhotoForIDCard(new PhotoHelper.OnTakePhotoResult() {
                    @Override
                    public void onSuccess(Intent intent, String s) {
                        if (TextUtils.isEmpty(s)) {
                            return;
                        }
                        filePath = s;
                        uploadFile();
                        Log.i("CGQ", filePath);
                    }

                    @Override
                    public void onFailure(String s) {

                    }
                });
            }
        });

        btnGetToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken();
            }
        });

    }

    private OSS oss;
    private String TAG = "CGQ";

    public void initAliyunOss() {
        Log.i(TAG, "初始化阿里云oss");
        //初始化阿里云oss
        String endpoint = "http://idcard-front.oss-cn-hangzhou.aliyuncs.com";

        //if null , default will be init
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
        conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
        conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
        conf.setMaxErrorRetry(2); // retry，default 2
        OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider("LTAIEfRCQiMBiRjw", "MR7howkHIyEVTecoHudg1yfmjrcHqb", "123");

        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider, conf);
    }

    public void creatBucket() {
        CreateBucketRequest createBucketRequest = new CreateBucketRequest("testcgq");
// 设置存储空间的访问权限为公共读，默认为私有读写。
        createBucketRequest.setBucketACL(CannedAccessControlList.PublicRead);
// 指定存储空间所在的地域。
        createBucketRequest.setLocationConstraint("oss-cn-hangzhou");
        OSSAsyncTask createTask = oss.asyncCreateBucket(createBucketRequest, new OSSCompletedCallback<CreateBucketRequest, CreateBucketResult>() {
            @Override
            public void onSuccess(CreateBucketRequest request, CreateBucketResult result) {
                Log.d("locationConstraint", request.getLocationConstraint());
            }

            @Override
            public void onFailure(CreateBucketRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常。
                if (clientException != null) {
                    // 本地异常，如网络异常等。
                    clientException.printStackTrace();
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

    public void uploadFile() {
        if (oss == null) {
            return;
        }
        // Construct an upload request
        PutObjectRequest put = new PutObjectRequest("testcgq", "testfile01", filePath);

// You can set progress callback during asynchronous upload
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
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // Request exception
                if (clientExcepion != null) {
                    // Local exception, such as a network exception
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // Service exception
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

// task.cancel(); // Cancel the task

// task.waitUntilFinished(); // Wait till the task is finished
    }

    public void turnTo(String token) {

        RPSDK.start(token, mContext,
                new RPSDK.RPCompletedListener() {
                    @Override
                    public void onAuditResult(RPSDK.AUDIT audit) {
                        Toast.makeText(mContext, audit + "", Toast.LENGTH_SHORT).show();
                        if (audit == RPSDK.AUDIT.AUDIT_PASS) { //认证通过
                        } else if (audit == RPSDK.AUDIT.AUDIT_FAIL) { //认证不通过
                        } else if (audit == RPSDK.AUDIT.AUDIT_IN_AUDIT) { //认证中，通常不会出现，只有在认证审核系统内部出现超时，未在限定时间内返回认证结果时出现。此时提示用户系统处理中，稍后查看认证结果即可。
                        } else if (audit == RPSDK.AUDIT.AUDIT_NOT) { //未认证，用户取消
                        } else if (audit == RPSDK.AUDIT.AUDIT_EXCEPTION) { //系统异常
                        }
                    }
                });
    }

    private String imageToBase64(String path) {
        File file = new File(path);
        String imgBase64 = null;
        byte[] content;
        try {
            content = new byte[(int) file.length()];
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(content);
            inputStream.close();
            imgBase64 = Base64.encodeToString(content, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return imgBase64;
        }
    }

    private String assetImageToBase64(String assetName) {

        File file;
        String imgBase64 = null;
        byte[] content;
        try {
            //file:///android_asset/idcard.jpg
            InputStream is = getAssets().open(assetName);//"idcard.jpg"
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            FileUtil.saveBitmap(bitmap);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            String path = FileUtil.getImgPath();
            file = new File(path);
            content = new byte[(int) file.length()];

            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(content);
            inputStream.close();
            imgBase64 = Base64.encodeToString(content, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return imgBase64;
        }
    }

    public void getToken() {

        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("登录中").setCanTouch(false).create();
        loadingDialog.show();

        final String imgBase64_f = assetImageToBase64("idcard.jpg");
        final String imgBase64_b = assetImageToBase64("idcard_back.jpg");

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("frontP", imgBase64_f);
        map2.put("backP", imgBase64_b);
        String str_map_param = JSON.toJSONString(map2);
        Log.d(TAG, str_map_param);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), str_map_param);
        HttpUtils.getIdAuthGetToken(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String token = jsonObject.get("token").toString();
                            turnTo(token);
                            //String uuid = jsonObject.get("uuid").toString();
                            //String userName = jsonObject.get("userName").toString();
                            //token = SecurityHelper.decryptData(token, uuid, SessionHelper.getClientPrivatekey());
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "登录失败：" + response.getMessage());
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
                        PopToastUtil.ShowToast((Activity) mContext, "登录出错了");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        loadingDialog.dismiss();
                    }
                });
    }

}
