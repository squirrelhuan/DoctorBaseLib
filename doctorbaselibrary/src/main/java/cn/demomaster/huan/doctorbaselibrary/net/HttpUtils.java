package cn.demomaster.huan.doctorbaselibrary.net;


import android.util.Log;

import cn.demomaster.huan.doctorbaselibrary.BuildConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class HttpUtils {


    private static HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
    //新建log拦截器
    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            Log.i("CGQ", "OkHttpMessage:" + message);
        }
    }).setLevel(level);

    static {
       if (BuildConfig.DEBUG) {
            //日志拦截器
            loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.i("CGQ", "OkHttpMessage:" + message);
                }
            });
            loggingInterceptor.setLevel(level);
        }
    }

    private static final int DEFAULT_TIMEOUT = 8; //连接 超时的时间，单位：秒
    private static final OkHttpClient client = new OkHttpClient.Builder().
            connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).addInterceptor(loggingInterceptor).
            readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).
            writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).build();


    private static HttpUtils httpUtils;
    private static Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;

    private synchronized static RetrofitInterface getRetrofit() {
        //初始化retrofit的配置
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URLConstant.URL_BASE)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            retrofitInterface = retrofit.create(RetrofitInterface.class);
        }
        return retrofitInterface;
    }

    //获取测试数据
    public static Observable<CommonApi> getTestData(int index, int pagesize) {
        return getRetrofit().getTestData(index, pagesize);
    }
    //创建session
    public static Observable<CommonApi> createSession() {
        return getRetrofit().getSession();
    }

    //验证session
    public static Observable<CommonApi> syncSession(RequestBody message) {
        return getRetrofit().getSyncSession(message);
    }

    //验证Token
    public static Observable<CommonApi> verifyToken(String message) {
        return getRetrofit().verifyToken(message);
    }

    //获取登陆数据
    public static Observable<CommonApi> getLogin(String message) {
        return getRetrofit().getLogin(message);
    }

    //获取验证码
    public static Observable<CommonApi> getSmsCode(String phoneNum) {
        return getRetrofit().getSmsCode(phoneNum);
    }

    //获取注册数据
    public static Observable<CommonApi> getRegister(String message) {
        return getRetrofit().getRegister(message);
    }

    //医生专业认证
    public static Observable<CommonApi> professionAuth(String message) {
        return getRetrofit().professionAuth(message);
    }

    //重置密码
    public static Observable<CommonApi> modifyPwd(String message) {
        return getRetrofit().modifyPwd(message);
    }

    //获取身份证ocr
    public static Observable<CommonApi> getCardOcrAuth(RequestBody message) {
        return getRetrofit().getCardOcrAuth(message);
    }

    //“获取所有的专业类型” 接口
    public static Observable<CommonApi> getAllCategory() {
        return getRetrofit().getAllCategory();
    }

    //根据专科查询医生列表
    public static Observable<CommonApi> getAllDoctorsInSpecifiedDomain(String message) {
        return getRetrofit().getAllDoctorsInSpecifiedDomain(message);
    }

    //“查询与客户预约请求参数相匹配的医生列表”接口
    public static Observable<CommonApi> getMatchedDoctorList(String message) {
        return getRetrofit().getMatchedDoctorList(message);
    }

    //查询指定医生信息
    public static Observable<CommonApi> getDoctorInfo(String message) {
        return getRetrofit().getDoctorInfo(message);
    }

    // “模糊查询医生” 接口
    public static Observable<CommonApi> fuzzyQueryDoctor(String message) {
        return getRetrofit().fuzzyQueryDoctor(message);
    }

    //获取指定医生的空闲时间”接口
    public static Observable<CommonApi> getTimeSchedule(String message) {
        return getRetrofit().getTimeSchedule(message);
    }

    //查询医生的历史订单评价
    public static Observable<CommonApi> getHistoryEvaluation(String message) {
        return getRetrofit().getHistoryEvaluation(message);
    }


    //根据专科发起预约
    public static Observable<CommonApi> findDoctorByCategory(String message) {
        return getRetrofit().findDoctorByCategory(message);
    }

    //添加病人
    public static Observable<CommonApi> addUserForPatient(String message) {
        return getRetrofit().addUserForPatient(message);
    }

    //获取病人列表数据
    public static Observable<CommonApi> getAllRelatedUsers(String message) {
        return getRetrofit().getAllRelatedUsers(message);
    }

    //医生端，获取个人资料页面信息
    public static Observable<CommonApi> getProfile(String message) {
        return getRetrofit().getProfile(message);
    }

    //获取病人数据
    public static Observable<CommonApi> getUserInfo(String message) {
        return getRetrofit().getUserInfo(message);
    }
    //获取病人数据
    public static Observable<CommonApi> getUserInfo(RequestBody message) {
        return getRetrofit().getUserInfo(message);
    }

    /*********     地址管理       ***************************************************************************/
    //获取默认地址数据
    public static Observable<CommonApi> getDefaultAddress(String message) {
        return getRetrofit().getDefaultAddress(message);
    }

    //获取地址列表
    public static Observable<CommonApi> getAllAddress(String message) {
        return getRetrofit().getAllAddress(message);
    }

    //设置默认地址
    public static Observable<CommonApi> setDefaultAddress(String message) {
        return getRetrofit().setDefaultAddress(message);
    }

    //地址簿中添加新地址
    public static Observable<CommonApi> eidtAddress(String message,boolean isEdit) {
        if(isEdit){
            return getRetrofit().modifyAddress(message);
        }else {
            return getRetrofit().addAddress(message);
        }
    }
    //地址簿中添加新地址
    public static Observable<CommonApi> addAddress(String message) {
        return getRetrofit().addAddress(message);
    }

    //地址簿中修改地址
    public static Observable<CommonApi> modifyAddress(String message) {
        return getRetrofit().modifyAddress(message);
    }


    //删除指定地址
    public static Observable<CommonApi> deleteAddress(String message) {
        return getRetrofit().deleteAddress(message);
    }


    /*********     订单管理       ***************************************************************************/

    //查询所有正在申请的订单
    public static Observable<CommonApi> getAllApplyingTrx(String message) {
        return getRetrofit().getAllApplyingTrx(message);
    }

    //查询所有正在申请的订单
    public static Observable<CommonApi> getAllTrxByState(int type,String message) {
        switch (type){
            case 0://查询所有正在申请的订单
                return getRetrofit().getAllApplyingTrx(message);
            case 1://查询所有进行中的订单
                return getRetrofit().getAllOngoingTrx(message);
            case 2://查询所有咨询结束的订单
                return getRetrofit().getAllClosedTrx(message);
        }
        return getRetrofit().getAllApplyingTrx(message);
    }

    //取消订单
    public static Observable<CommonApi> cancelTrx(String message) {
        return getRetrofit().cancelTrx(message);
    }
    //接收订单
    public static Observable<CommonApi> acceptAppointmentRequest(String message) {
        return getRetrofit().acceptAppointmentRequest(message);
    }
    //拒絕订单
    public static Observable<CommonApi> rejectAppointmentRequest(String message) {
        return getRetrofit().rejectAppointmentRequest(message);
    }

    //医生端/客户端, 同意修改订单（老接口）
    public static Observable<CommonApi> approveAppointTimeChange(String message) {
        return getRetrofit().approveAppointTimeChange(message);
    }
    //医生端/客户端, 拒绝修改订单  （老接口）
    public static Observable<CommonApi> refuseAppointTimeChange(String message) {
        return getRetrofit().refuseAppointTimeChange(message);
    }

    //医生端/客户端, 发起修改订单预约时间
    public static Observable<CommonApi> changeAppointmentTime(String message) {
        return getRetrofit().changeAppointmentTime(message);
    }
    //医生端/客户端, 拒绝并发起修改订单预约时间
    public static Observable<CommonApi> proposeNewAppointTimeChange(String message) {
        return getRetrofit().proposeNewAppointTimeChange(message);
    }

    //医生端/客户端, 保留订单(修改订单时间不能达成一致)
    public static Observable<CommonApi> keepPreviousTime(String message) {
        return getRetrofit().keepPreviousTime(message);
    }

    //客户端，提交预约请求页面，收费标准提醒
    public static Observable<CommonApi> getChargeTips(RequestBody message) {
        return getRetrofit().getChargeTips(message);
    }
    /***************** 留言 ****************************/
    //获取用户系统默认姓名和电话
    public static Observable<CommonApi> getDefaultInfo(RequestBody message) {
        return getRetrofit().getDefaultInfo(message);
    }
    //提交用户留言到后台
    public static Observable<CommonApi> recordNewMessage(RequestBody message) {
        return getRetrofit().recordNewMessage(message);
    }

    //修改用户头像照片(两端通用)
    public static Observable<CommonApi> changePhoto(String message) {
        return getRetrofit().changePhoto(message);
    }


    //获取实人认证token
    public static Observable<CommonApi> getIdAuthGetToken(RequestBody message) {
        return getRetrofit().getIdAuthGetToken(message);
    }
    // 服务器查询认证结果，如成功则保存证件照和手持证件照到数据库，并更新认证状态
    public static Observable<CommonApi> getAcsResponse(String message) {
        return getRetrofit().getAcsResponse(message);
    }
    //  预约前检查用户是否完成了实人认证
    public static Observable<CommonApi> requestCheck(String message) {
        return getRetrofit().requestCheck(message);
    }


    //获取OSS访问令牌
    public static Observable<CommonApi> getAssumeRole(RequestBody message) {
        return getRetrofit().getAssumeRole(message);
    }

    /***************** DOCTOR ****************************/

    //医生是否专业认证
    public static Observable<CommonApi> isProfessionAuth(String message) {
        return getRetrofit().isProfessionAuth(message);
    }

    //获取医生最近两周内的时间安排（包括当天）/ 指定日期的时间安排
    public static Observable<CommonApi> getTimeArrangement(String message) {
        return getRetrofit().getTimeArrangement(message);
    }

    //医生端, 首页 预约请求, 咨询订单, 完成订单入口
    public static Observable<CommonApi> getTrxStatisticInfo(String message) {
        return getRetrofit().getTrxStatisticInfo(message);
    }

    //医生添加可预约时间段
    public static Observable<CommonApi> updateTimeSchedule(String message) {
        return getRetrofit().updateTimeSchedule(message);
    }

    //医生修改某一可预约时间段
    public static Observable<CommonApi> modifyTimeArrangement(String message) {
        return getRetrofit().modifyTimeArrangement(message);
    }


    //获取新的预约请求
    public static Observable<CommonApi> getNewAppointmentRequest(String message) {
        return getRetrofit().getNewAppointmentRequest(message);
    }

    //获取医院列表
    public static Observable<CommonApi> getHospitalList(String message) {
        return getRetrofit().getHospitalList(message);
    }

    //根据中文或者拼音首字母查找医院
    public static Observable<CommonApi> getHospitalListBySpell(String message) {
        return getRetrofit().getHospitalListBySpell(message);
    }

    //医生端, 获取咨询中订单
    public static Observable<CommonApi> getOrderComplateByType(int type,String message) {
        switch (type){
            case 0://医生端, 获取咨询中订单
                return getRetrofit().getWatingDiagnosingTrx(message);
            case 1://医生端, 获取待咨询订单
                return getRetrofit().getWatingDiagnosingTrx(message);
            case 2://医生端, 获取待咨询中订单
                return getRetrofit().getDiagnoseOngoingTrx(message);
            case 3://医生端, 获取已咨询订单
                return getRetrofit().getDiagnoseCompleteTrx(message);
        }
        return null;
    }

    //医生端, 获取咨询中订单
    public static Observable<CommonApi> getOrderIngByType(int type,String message) {
        switch (type){
            case 0://医生端, 待咨询订单
                return getRetrofit().getWatingDiagnosingTrx(message);
            case 1://医生端, 咨询中订单
                return getRetrofit().getDiagnoseOngoingTrx(message);
            case 2://医生端, 获取待支付订单
                return getRetrofit().getDiagnoseCompleteTrx(message);
        }
        return null;
    }


}
