package cn.demomaster.huan.doctorbaselibrary.util;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;

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
import java.util.Map;

/**
 * @author squirrel桓
 * @date 2019/1/3.
 * description：
 */
public class OssUtil {

    /*private OssUtil instance;

    public OssUtil getInstance() {
        if(instance==null){

        }
        return instance;
    }*/

    public static interface OnOosListener{
        void onSuccess(String url);
        void onFail();
    }

    public  void getAssumeRole(final Context mContext, final String filePath, final OnOosListener onOosListener) {


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
                            initAliyunOss(mContext,accessKeyId, accessKeySecret, securityToken,filePath,onOosListener);
                            Log.i(TAG, "userdata=");
                        } else {
                            onOosListener.onFail();
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
                        onOosListener.onFail();
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
    public void initAliyunOss(final Context mContext, String accessKeyId, String secretKeyId, String securityToken, String filePath, OnOosListener onOosListener) {
        Log.i(TAG, "初始化阿里云oss");
        //初始化阿里云oss
        //String endpoint = "http://idcard-front.oss-cn-hangzhou.aliyuncs.com";
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

        //if null , default will be init
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000);// connction time out default 15s
        conf.setSocketTimeout(15 * 1000);// socket timeout，default 15s
        conf.setMaxConcurrentRequest(5);// synchronous request number，default 5
        conf.setMaxErrorRetry(2);// retry，default 2
        OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv
        //OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider("LTAIEfRCQiMBiRjw", "MR7howkHIyEVTecoHudg1yfmjrcHqb", "123");
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, secretKeyId, securityToken);
        oss = new OSSClient(mContext.getApplicationContext(), endpoint, credentialProvider, conf);
        //初始化完成开始上传照片
        uploadFile(filePath,onOosListener);
    }

    String bucketName = "drvisit-photo";
    public void uploadFile(String filePath, final OnOosListener onOosListener) {
        if (oss == null) {
            onOosListener.onFail();
            return;
        }
        final String imageName = bucketName + "-" + SessionHelper.getUserName() + "-" + System.currentTimeMillis();
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(bucketName, imageName, filePath);
        // 异步上传时可以设置进度回调。
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject","currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                // Log.d("url=", url);
                String url = oss.presignPublicObjectURL(bucketName, imageName);
                Log.d("url1=", url);
                if (url != null) {
                    //changePhoto(url);
                    onOosListener.onSuccess(url);
                }else {
                    onOosListener.onFail();
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                onOosListener.onFail();
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
