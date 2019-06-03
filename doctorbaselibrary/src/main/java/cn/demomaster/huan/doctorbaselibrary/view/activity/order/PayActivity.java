package cn.demomaster.huan.doctorbaselibrary.view.activity.order;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.Constants;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.*;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;
import static java.util.Collections.sort;

public class PayActivity extends BaseActivity {


    public static String sgin = "";
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

       /* api = WXAPIFactory.createWXAPI(this, "wxb4ba3c02aa476ea1");*/
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将该app注册到微信
        api.registerApp(Constants.APP_ID);
        findViewById(R.id.btn_test_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrderInfo();
            }
        });
        findViewById(R.id.btn_check_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                Toast.makeText(PayActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getOrderInfo() {

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        map2.put("trxId", "40");

        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        retrofitInterface.wxpayGetPayOrder(body)//getRepeatTimePeriodById
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext:" + JSON.toJSONString(response.getData()));
                        if (response.getRetCode() == 0) {
                            com.alibaba.fastjson.JSONObject json = JSON.parseObject(JSON.toJSONString(response.getData()));
                            if(json.containsKey("partnerid")) {
                               /* partnerid = jsonObject.get("partnerid").toString();
                                prepayId = jsonObject.get("prepayid").toString();
                                packageValue = jsonObject.get("package").toString();
                                nonceStr = jsonObject.get("noncestr").toString();
                                timeStamp = jsonObject.get("timestamp").toString();
                                sign = jsonObject.get("sign").toString();*/
                                PayReq req = new PayReq();
                                //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                                req.appId = json.getString("appid");
                                req.partnerId = json.getString("partnerid");
                                req.prepayId = json.getString("prepayid");
                                req.nonceStr = json.getString("noncestr");
                                req.timeStamp = json.getString("timestamp");
                                req.packageValue = json.getString("package");
                                req.sign = json.getString("sign");
                                req.extData = "app data"; // optional
                                //Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                api.sendReq(req);

                                //sgin =json.getString("sign");

                                //WX_Pay pay = new WX_Pay(mContext);
                                //pay.pay(json.getString("appid"),json.getString("partnerid"),json.getString("prepayid"));
                            }
                            Log.i(TAG, "onNext:成功 ");
                        } else {
                            Log.i(TAG, "onNext:失败 " );
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

}
