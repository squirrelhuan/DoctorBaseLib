package cn.demomaster.huan.doctorbaselibrary.net;


import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.*;


public interface RetrofitInterface {

    //获取测试数据
    @FormUrlEncoded
    @POST(URLConstant.URL_BASE)
    Observable<CommonApi> getTestData(@Field("index") int index,
                                      @Field("pagesize") int pagesize);

    //获取session
    @GET(URLConstant.USER_CREATE_SESSION)
    Observable<CommonApi> getSession();

    //验证session
    //@FormUrlEncoded
    //@POST(URLConstant.USER_SYNC_SESSION)
    //Observable<CommonApi> getSyncSession(@Field("message") String message);
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.USER_SYNC_SESSION)
    Observable<CommonApi> getSyncSession(@Body RequestBody body);

    //验证Token
    @FormUrlEncoded
    @POST(URLConstant.USER_VERIFY_TOKEN)
    Observable<CommonApi> verifyToken(@Field("message") String message);


    //获取登陆数据
    @FormUrlEncoded
    @POST(URLConstant.USER_LOGIN)
    Observable<CommonApi> getLogin(@Field("message") String message);


    //获取短信验证码
    @FormUrlEncoded
    @POST(URLConstant.USER_SMS_CODE)
    Observable<CommonApi> getSmsCode(@Field("phoneNum") String phoneNum);

    //获取注册数据
    @FormUrlEncoded
    @POST(URLConstant.USER_REGISTER)
    Observable<CommonApi> getRegister(@Field("message") String message);

    //医生专业认证
    @FormUrlEncoded
    @POST(URLConstant.DOCTOR_PROFESSION_AUTH)
    Observable<CommonApi> professionAuth(@Field("message") String message);

    //医生信息修改
    @FormUrlEncoded
    @POST(URLConstant.DOCTOR_MODIFY_PROFILE)
    Observable<CommonApi> modifyProfile(@Field("message") String message);

    //重置密码
    @FormUrlEncoded
    @POST(URLConstant.USER_MODIFYPWD)
    Observable<CommonApi> modifyPwd(@Field("message") String message);

    //获取身份证ocr
   /* @FormUrlEncoded
    @POST(URLConstant.USER_Card_Ocr_Auth)
    Observable<CommonApi> getCardOcrAuth(@Field("message") String message);*/
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.USER_Card_Ocr_Auth)
    Observable<CommonApi> getCardOcrAuth(@Body RequestBody body);


    //根据专科查询医生列表
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_GET_All_DOCTORS_IN_SPECIFIED_DOMAIN)
    Observable<CommonApi> getAllDoctorsInSpecifiedDomain(@Field("message") String message);

    //“查询与客户预约请求参数相匹配的医生列表”接口
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_GET_MATCHED_DOCTOR_LIST)
    Observable<CommonApi> getMatchedDoctorList(@Field("message") String message);

    //“获取所有的专业类型” 接口
    @GET(URLConstant.YIDAO_GET_ALL_CATEGORY)
    Observable<CommonApi> getAllCategory();


    //查询指定医生信息
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_GET_DOCTOR_INFO)
    Observable<CommonApi> getDoctorInfo(@Field("message") String message);

    //模糊查询医生
    @GET(URLConstant.USER_FUZZY_QUERY_DOCTOR)
    Observable<CommonApi> fuzzyQueryDoctor(@Query("context") String context);

    //获取指定医生的空闲时间”接口
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_GET_TIME_SCHEDULE)
    Observable<CommonApi> getTimeSchedule(@Field("message") String message);

    //查询医生的历史订单评价
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_GET_HISTORY_EVALUATION)
    Observable<CommonApi> getHistoryEvaluation(@Field("message") String message);

    //医生端, 获取已结束订单详情
    @FormUrlEncoded
    @POST(URLConstant.GET_CLOSED_TRX_DETAILED_INFO)
    Observable<CommonApi> getClosedTrxDetailedInfo(@Field("message") String message);

    //用户端，获取订单详情页
    @FormUrlEncoded
    @POST(URLConstant.GET_TRX_DETAILED_INFO_FOR_PATIENT)
    Observable<CommonApi> getTrxDetailedInfoForPatient(@Field("message") String message);

    //根据专科发起预约
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_FIND_DOCTOR_BY_CATEGORY)
    Observable<CommonApi> findDoctorByCategory(@Field("message") String message);

    //新增病人
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_ADD_USER_FOR_PATIENT)
    Observable<CommonApi> addUserForPatient(@Field("message") String message);

    //用户端，删除副用户
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_USER_DELDEPUTYUSER)
    Observable<CommonApi> delDeputyUser(@Field("message") String message);

    //获取病人列表数据
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_GET_All_RELATED_USERS)
    Observable<CommonApi> getAllRelatedUsers(@Field("message") String message);

    //医生端，获取个人资料页面信息
    @FormUrlEncoded
    @POST(URLConstant.DOCTOR_GET_PROFILE)
    Observable<CommonApi> getProfile(@Field("message") String message);

    //医生端，重复时间管理页面，获取重复规则
    @FormUrlEncoded
    @POST(URLConstant.GET_REPEAT_TIME_SCHEDULES)
    Observable<CommonApi> getRepeatTimeSchedules(@Field("message") String message);

    //医生端，编辑重复时间设置规则
    @FormUrlEncoded
    @POST(URLConstant.EDIT_REPEAT_TIME_SCHEDULE)
    Observable<CommonApi> editRepeatTimeSchedule(@Field("message") String message);


    //获取病人数据
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_GET_USER_INFO)
    Observable<CommonApi> getUserInfo(@Field("message") String message);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.YIDAO_GET_USER_INFO)
    Observable<CommonApi> getUserInfo(@Body RequestBody body);

    //获取默认地址数据
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_GET_DEFAULT_ADDRESS)
    Observable<CommonApi> getDefaultAddress(@Field("message") String message);


    //获取地址列表
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_GET_ALL_ADDRESS)
    Observable<CommonApi> getAllAddress(@Field("message") String message);

    //设置默认地址
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_SET_DEFAULT_ADDRESS)
    Observable<CommonApi> setDefaultAddress(@Field("message") String message);

    //地址簿中添加新地址
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_SET_ADD_ADDRESS)
    Observable<CommonApi> addAddress(@Field("message") String message);

    //地址簿中修改地址
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_SET_MODIFY_ADDRESS)
    Observable<CommonApi> modifyAddress(@Field("message") String message);

    //删除指定地址
    @FormUrlEncoded
    @POST(URLConstant.YIDAO_DELETE_ADDRESS)
    Observable<CommonApi> deleteAddress(@Field("message") String message);

    //查询所有正在申请的订单
    @FormUrlEncoded
    @POST(URLConstant.GET_ALL_APPLYING_TRX)
    Observable<CommonApi> getAllApplyingTrx(@Field("message") String message);

    //查询所有咨询结束的订单
    @FormUrlEncoded
    @POST(URLConstant.GET_ALL_CLOSED_TRX)
    Observable<CommonApi> getAllClosedTrx(@Field("message") String message);

    //查询所有进行中的订单
    @FormUrlEncoded
    @POST(URLConstant.GET_ALL_ONGOING_TRX)
    Observable<CommonApi> getAllOngoingTrx(@Field("message") String message);


    //医生端, 已完成咨询订单页面
    @FormUrlEncoded
    @POST(URLConstant.CLOSED_TRX_INFO)
    Observable<CommonApi> closedTrxInfo(@Field("message") String message);


    //取消订单
    @FormUrlEncoded
    @POST(URLConstant.CANCEL_TRX)
    Observable<CommonApi> cancelTrx(@Field("message") String message);

    //接收订单
    @FormUrlEncoded
    @POST(URLConstant.ACCEPT_APPOINTMENT_REQUEST)
    Observable<CommonApi> acceptAppointmentRequest(@Field("message") String message);

    //拒絕订单
    @FormUrlEncoded
    @POST(URLConstant.REJECT_APPOINTMENT_REQUEST)
    Observable<CommonApi> rejectAppointmentRequest(@Field("message") String message);

    //医生端/客户端,修改订单  （老接口）
    @FormUrlEncoded
    @POST(URLConstant.APPROVE_APPOINT_TIME_CHANGE)
    Observable<CommonApi> approveAppointTimeChange(@Field("message") String message);

    //医生端/客户端, 同意修改订单（老接口）
    @FormUrlEncoded
    @POST(URLConstant.REFUSE_APPOINT_TIME_CHANGE)
    Observable<CommonApi> refuseAppointTimeChange(@Field("message") String message);

    //医生端/客户端, 发起修改订单预约时间
    @FormUrlEncoded
    @POST(URLConstant.CHANGE_APPOINTMENT_TIME)
    Observable<CommonApi> changeAppointmentTime(@Field("message") String message);

    //医生端/客户端, 拒绝并发起修改订单预约时间
    @FormUrlEncoded
    @POST(URLConstant.PROPOSE_NEW_APPOINTTIME_CHANGE)
    Observable<CommonApi> proposeNewAppointTimeChange(@Field("message") String message);

    //医生端/客户端, 保留订单(修改订单时间不能达成一致)
    @FormUrlEncoded
    @POST(URLConstant.KEEP_PREVIOUS_TIME)
    Observable<CommonApi> keepPreviousTime(@Field("message") String message);

    //客户端，提交预约请求页面，收费标准提醒
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.GET_CHARGE_TIPS)
    Observable<CommonApi> getChargeTips(@Body RequestBody body);

    //获取用户系统默认姓名和电话
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.GET_DEFAULT_INFO)
    Observable<CommonApi> getDefaultInfo(@Body RequestBody body);

    //提交用户留言到后台
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.RECORD_NEW_MESSAGE)
    Observable<CommonApi> recordNewMessage(@Body RequestBody body);

    //修改用户头像照片(两端通用)
    @FormUrlEncoded
    @POST(URLConstant.USER_CHANGE_PHOTO)
    Observable<CommonApi> changePhoto(@Field("message") String message);


    //获取OSS访问令牌
    //@FormUrlEncoded
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.YIDAO_GET_OSS_ASSUME_ROLE)
    Observable<CommonApi> getAssumeRole(@Body RequestBody message);

    //获取实人认证token
    //@FormUrlEncoded
   // @POST(URLConstant.GET_GET_ID_AUTH_GET_TOKEN)
    //Observable<CommonApi> getIdAuthGetToken(@Field("message") String message);
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.GET_GET_ID_AUTH_GET_TOKEN)
    Observable<CommonApi> getIdAuthGetToken(@Body RequestBody body);

    // 服务器查询认证结果，如成功则保存证件照和手持证件照到数据库，并更新认证状态
    @FormUrlEncoded
    @POST(URLConstant.GET_ACS_RESPONSE)
    Observable<CommonApi> getAcsResponse(@Field("message") String message);

    // 预约前检查用户是否完成了实人认证
    @FormUrlEncoded
    @POST(URLConstant.REQUEST_CHECK)
    Observable<CommonApi> requestCheck(@Field("message") String message);


    /***************** DOCTOR ****************************/

    //医生是否专业认证
    @FormUrlEncoded
    @POST(URLConstant.IS_PROFESSION_AUTH)
    Observable<CommonApi> isProfessionAuth(@Field("message") String message);

    //获取医生最近两周内的时间安排（包括当天）/ 指定日期的时间安排
    @FormUrlEncoded
    @POST(URLConstant.GET_TIME_ARRANGEMENT)
    Observable<CommonApi> getTimeArrangement(@Field("message") String message);

    //医生端, 首页 预约请求, 咨询订单, 完成订单入口
    @FormUrlEncoded
    @POST(URLConstant.GET_TRX_STATI_STICINFO)
    Observable<CommonApi> getTrxStatisticInfo(@Field("message") String message);

    //医生添加可预约时间段
    @FormUrlEncoded
    @POST(URLConstant.UPDATE_TIME_SCHEDULE)
    Observable<CommonApi> updateTimeSchedule(@Field("message") String message);

    //修改某一可预约时间段
    @FormUrlEncoded
    @POST(URLConstant.MODIFY_TIME_ARRANGEMENT)
    Observable<CommonApi> modifyTimeArrangement(@Field("message") String message);

    //获取医生最近两周内的时间安排（包括当天）/ 指定日期的时间安排
    @FormUrlEncoded
    @POST(URLConstant.GET_NEW_APPOINTMENT_REQUEST)
    Observable<CommonApi> getNewAppointmentRequest(@Field("message") String message);

    //获取医院列表
    @FormUrlEncoded
    @POST(URLConstant.GET_HOSPITAL_LIST)
    Observable<CommonApi> getHospitalList(@Field("message") String message);

    //根据中文或者拼音首字母查找医院
    @FormUrlEncoded
    @POST(URLConstant.GET_HOSPITAL_LIST_BY_SPELL)
    Observable<CommonApi> getHospitalListBySpell(@Field("message") String message);


    //医生端, 获取咨询中订单
    @FormUrlEncoded
    @POST(URLConstant.GET_DIAGNOSE_ONGOING_TRX)
    Observable<CommonApi> getDiagnoseOngoingTrx(@Field("message") String message);


    //医生端, 获取待咨询订单
    @FormUrlEncoded
    @POST(URLConstant.GET_WATINGD_IAGNOSING_TRX)
    Observable<CommonApi> getWatingDiagnosingTrx(@Field("message") String message);

    //医生端, 获取已咨询订单
    @FormUrlEncoded
    @POST(URLConstant.GET_DIAGNOSE_COMPLETE_TRX)
    Observable<CommonApi> getDiagnoseCompleteTrx(@Field("message") String message);

    //医生端, 医生端获取二维码
    @FormUrlEncoded
    @POST(URLConstant.GENERATEQR)
    Observable<CommonApi> generateQR(@Field("message") String message);

    //客户端扫描医生二维码, 获取医生【医师资格证书】，同时返回订单name
    @FormUrlEncoded
    @POST(URLConstant.GET_DOCTOR_INFO_BY_QR)
    Observable<CommonApi> getDoctorInfoByQR(@Field("message") String message);

    // “订单开始计时” 接口
    @FormUrlEncoded
    @POST(URLConstant.BEGIN_TIME_KEEPING)
    Observable<CommonApi> beginTimeKeeping(@Field("message") String message);

    // “订单结束计时” 接口
    @FormUrlEncoded
    @POST(URLConstant.STOP_TIME_KEEPING)
    Observable<CommonApi> stopTimeKeeping(@Field("message") String message);


    //医生端, 新的预约请求, 接单前允许三次沟通来了解病情
    @FormUrlEncoded
    @POST(URLConstant.PRE_COMMUNICATION)
    Observable<CommonApi> preCommunication(@Field("message") String message);

    //医生端，查询申请中订单 （老接口，但改了返回，添加了专家和病人互动信息）
    @FormUrlEncoded
    @POST(URLConstant.PRE_COMMUNICATION_REPLY)
    Observable<CommonApi> preCommunicationReply(@Field("message") String message);

    //获取用户证件类型
    @FormUrlEncoded
    @POST(URLConstant.GET_ID_TYPE)
    Observable<CommonApi> getIdType(@Field("message") String message);

    //医生端， 计时过程中app被关闭
    @FormUrlEncoded
    @POST(URLConstant.GET_TRX_IN_DIAGNOSING)
    Observable<CommonApi> getTrxInDiagnosing(@Field("message") String message);

    //客户端， 计时过程中app被关闭
    @FormUrlEncoded
    @POST(URLConstant.GET_TRX_IN_DIAGNOSING_PATIENT)
    Observable<CommonApi> getTrxInDiagnosing_Patient(@Field("message") String message);

    //客户端，“咨询结束,病人对医生评价” 接口
    @FormUrlEncoded
    @POST(URLConstant.EVALUATE_SERVICE)
    Observable<CommonApi> evaluateService(@Field("message") String message);

    //医生端, 对病人进行评价
    @FormUrlEncoded
    @POST(URLConstant.EVALUATE_PATIENT)
    Observable<CommonApi> evaluatePatient(@Field("message") String message);


    //医生端，获取重复时间段
    @FormUrlEncoded
    @POST(URLConstant.GET_REPEAT_TIME_PERIOD_BY_ID)
    Observable<CommonApi> getRepeatTimePeriodById(@Field("message") String message);

    //医生端，医生填写咨询意见，查询手术方案
    @FormUrlEncoded
    @POST(URLConstant.GET_OPERATION_BY_ICD9)
    Observable<CommonApi> getOperationByICD9(@Field("message") String message);

    //医生端，医生填写咨询意见，病情确诊
    @FormUrlEncoded
    @POST(URLConstant.GET_DISEASE_BY_ICD10)
    Observable<CommonApi> getDiseaseByICD10(@Field("message") String message);

    //医生端，填写咨询意见
    @FormUrlEncoded
    @POST(URLConstant.RECORD_DIAGNOSE_ADVISORY)
    Observable<CommonApi> recordDiagnoseAdvisory(@Field("message") String message);

    //医生端，订单咨询意见填写完成后, 更新医生的平台虚拟账户金额
    @FormUrlEncoded
    @POST(URLConstant.VACCOUNTPAY)
    Observable<CommonApi> vAccountPay(@Field("message") String message);

    //医生端，删除当天的该可预约时间段  （只有删除将来所有该可重复时间段才要）
    @FormUrlEncoded
    @POST(URLConstant.INVALID_REPEAT_TIME_SCHEDULE)
    Observable<CommonApi> invalidRepeatTimeSchedule(@Field("message") String message);

    //医生端，删除当天的该可预约时间段
    @FormUrlEncoded
    @POST(URLConstant.DELETE_TIME_ARRANGEMENT)
    Observable<CommonApi> deleteTimeArrangement(@Field("message") String message);

    //医生端，删除重复时间规则
    @FormUrlEncoded
    @POST(URLConstant.DELETE_REPEAT_TIME_SCHEDULE)
    Observable<CommonApi> deleteRepeatTimeSchedule(@Field("message") String message);

    //极光推送，客户端将Registration ID传给服务器
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.SEND_RECORDREGIID)
    Observable<CommonApi> recordRegiId(@Body RequestBody body);

    //客户端】微信支付第一步，统一下单
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.WXPAY_GET_PAY_ORDER)
    Observable<CommonApi> wxpayGetPayOrder(@Body RequestBody body);

    //支付宝支付，返回APP支付订单信息
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.ALIPAY_GET_PAYORDER)
    Observable<CommonApi> alipayGetPayOrder(@Body RequestBody body);

    //获取医生端虚拟账户余额
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.GET_ACCOUNT_AMOUNT)
    Observable<CommonApi> getAccountAmount(@Body RequestBody body);

    //医生端，提现到银行卡
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.WITHDRAW)
    Observable<CommonApi> withdraw(@Body RequestBody body);

    //医生端，绑定银行借记卡
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.BIND_BANK_ACCOUNT)
    Observable<CommonApi> bindBankAccount(@Body RequestBody body);

    //客户端人员资料，修改手机号
    @FormUrlEncoded
    @POST(URLConstant.CHANGE_PHONENUM)
    Observable<CommonApi> changePhoneNum(@Field("message") String message);

    //医生端，获取银行卡列表
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.GET_CARD_LIST)
    Observable<CommonApi> getCardList(@Body RequestBody body);

    //绑定银行卡页面，获取用户姓名，打掩码
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.GET_ACCOUNT_NAME)
    Observable<CommonApi> getAccountName(@Body RequestBody body);

    //客户端，获取优惠券列表
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.GET_DISCOUNT_TICKETS)
    Observable<CommonApi> getDiscountTickets(@Body RequestBody body);

    //用户端，支付页面订单信息
    @FormUrlEncoded
    @POST(URLConstant.GET_PAY_TRX_DETAILED_INFO_FOR_PATIENT)
    Observable<CommonApi> getPayTrxDetailedInfoForPatient(@Field("message") String message);

    //客户端，添加疾病历史
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.ADD_DISEASE_HISTORY)
    Observable<CommonApi> addDiseaseHistory(@Body RequestBody body);

    //客户端，添加用户过敏药物
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.ADD_ALLERGIC_DRUGS)
    Observable<CommonApi> addAllergicDrugs(@Body RequestBody body);

    //意见反馈，医生端获取订单列表信息
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.GET_TRX_INFOD)
    Observable<CommonApi> getTrxInfoD(@Body RequestBody body);

    //意见反馈，用户端获取订单列表信息
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(URLConstant.GET_TRX_INFOP)
    Observable<CommonApi> getTrxInfoP(@Body RequestBody body);

}
