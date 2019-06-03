package cn.demomaster.huan.doctorbaselibrary.view.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import cn.demomaster.huan.quickdeveloplibrary.camera.idcard.CameraIDCardActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.PhotoHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class IDCardActivity extends BaseActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);
        imageView = (ImageView) findViewById(R.id.main_image);
    }

    private String TAG ="CGQ";
    private void getOcrData(String imgBase64) {
        Map map = new HashMap();
        map.put("name", "褚国庆");
        map.put("identityType", "0");
        map.put("imageBase64",imgBase64);
        String map_str = JSON.toJSONString(map);
        Log.d(TAG, map_str);
       /* String str = SecurityHelper.jsSign(map_str, true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId",SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("token", SessionHelper.getToken());//p是病人端，d是医生端
        map2.put("userData", rsaModel.getEncryptData());
        map2.put("uuid", rsaModel.getUuid());
        String map_str2 = JSON.toJSONString(map2);*/

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),map_str);
        //Log.d(TAG, map_str2);
        HttpUtils.getCardOcrAuth(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //成功获取公钥，保存到本地
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            /*String publicKey = jsonObject.get("publicKey").toString();
                            String sessionId = jsonObject.get("sessionId").toString();
                            Log.i(TAG, "成功获取 publicKey: " + publicKey);
                            SessionHelper.setPublicKey(publicKey);
                            SessionHelper.setSessionId(sessionId);*/
                            Log.i(TAG, "成功获取 ocr " + JSON.toJSONString(response));
                        } else {
                            Log.i(TAG,"fail:" + response.getMessage());
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

    private String imageToBase64(String path) {

        File file = new File(path);
        String imgBase64 =null ;
        byte[] content = new byte[(int) file.length()];
        try {
            //file:///android_asset/idcard.jpg
            /*InputStream is = getAssets().open("idcard.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            FileUtil.saveBitmap(bitmap);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            path = FileUtil.getImgPath();
            file = new File(path);*/
            content = new byte[(int) file.length()];

            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(content);
            inputStream.close();
            imgBase64 = Base64.encodeToString( content,Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return imgBase64;
        }
    }

    /**
     * 拍摄证件照片
     *
     * @param type 拍摄证件类型
     */
    private void takePhoto(int type) {
        photoHelper.takePhotoForIDCard(new PhotoHelper.OnTakePhotoResult() {
            @Override
            public void onSuccess(Intent intent, String s) {
                if (!TextUtils.isEmpty(s)) {
                    imageView.setImageBitmap(BitmapFactory.decodeFile(s));
                }

                final String imgBase64 = imageToBase64(s);
                createSession(new OnCreateSessionListener() {
                    @Override
                    public void onSuccess() {
                        getOcrData(imgBase64);
                    }

                    @Override
                    public void onFail(String error) {
                        PopToastUtil.ShowToast((Activity) mContext, "身份识别失败：" + error);
                    }
                });

                Log.i("CGQ",imgBase64);
            }

            @Override
            public void onFailure(String s) {

            }
        });

    }

    /**
     * 身份证正面
     */
    public void frontIdCard(View view) {
        takePhoto(CameraIDCardActivity.TYPE_ID_CARD_FRONT);
    }

    /**
     * 身份证反面
     */
    public void backIdCard(View view) {
        takePhoto(CameraIDCardActivity.TYPE_ID_CARD_BACK);
    }
}
