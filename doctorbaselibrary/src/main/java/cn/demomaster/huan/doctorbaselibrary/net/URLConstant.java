package cn.demomaster.huan.doctorbaselibrary.net;


public class URLConstant {
    public static final String URL_BASE = "http://www.fegofficial.com:4576/";

    //服务器地址http://192.168.31.51:4576
    //public static final String SERVER = "http://47.96.162.168:8080/";
    public static final String SERVER = "http://47.107.226.147/";
    //public static final String SERVER2 = "http://47.96.162.168:8877/";

    //创建用户session
    public static final String USER_CREATE_SESSION = SERVER
            + "yidao/user/createSession ";

    //验证用户session
    public static final String USER_SYNC_SESSION = SERVER
            + "yidao/user/syncSession";

    //验证用户token
    public static final String USER_VERIFY_TOKEN = SERVER
            + "yidao/user/verifyToken";

    //用户登陆
    public static final String USER_LOGIN = SERVER
            + "yidao/user/login";

    //验证码
    public static final String USER_SMS_CODE = SERVER
            + "yidao/smsAuth/sendSms";

    //用户注册
    public static final String USER_REGISTER = SERVER
            + "yidao/user/register";

    //医生专业认证
    public static final String DOCTOR_PROFESSION_AUTH = SERVER
            + "yidao/doctor/professionAuth";

    //医生信息修改
    public static final String DOCTOR_MODIFY_PROFILE = SERVER
            + "yidao/doctor/modifyProfile";

    //重置密码
    public static final String USER_MODIFYPWD = SERVER
            + "yidao/pwd/modifyPwd";

    //获取身份证ocr
    public static final String USER_Card_Ocr_Auth = SERVER
            + "yidao/user/cardOCRAuth";

    //根据专科查询医生列表
    public static final String YIDAO_GET_All_DOCTORS_IN_SPECIFIED_DOMAIN = SERVER
            + "yidao/patient/getAllDoctorsInSpecifiedDomain";

    //“查询与客户预约请求参数相匹配的医生列表”接口
    public static final String YIDAO_GET_MATCHED_DOCTOR_LIST = SERVER
            + "yidao/patient/getMatchedDoctorList";

    //“获取所有的专业类型” 接口
    public static final String YIDAO_GET_ALL_CATEGORY = SERVER
            + "yidao/patient/getAllCategory";

    //查询指定医生信息
    public static final String YIDAO_GET_DOCTOR_INFO = SERVER
            + "yidao/patient/getDoctorInfo";

    //模糊查询医生
    public static final String USER_FUZZY_QUERY_DOCTOR = SERVER
            + "yidao/user/fuzzyQueryDoctor";

    //获取指定医生的空闲时间”接口
    public static final String YIDAO_GET_TIME_SCHEDULE = SERVER
            + "yidao/patient/getTimeSchedule";

    //查询医生的历史订单评价
    public static final String YIDAO_GET_HISTORY_EVALUATION = SERVER
            + "yidao/patient/getHistoryEvaluation";

    //医生端, 获取已结束订单详情
    public static final String GET_CLOSED_TRX_DETAILED_INFO = SERVER
            + "yidao/trx/getClosedTrxDetailedInfo";

    //用户端，获取订单详情页
    public static final String GET_TRX_DETAILED_INFO_FOR_PATIENT = SERVER
            + "yidao/patient/getTrxDetailedInfoForPatient";

    //根据专科发起预约
    public static final String YIDAO_FIND_DOCTOR_BY_CATEGORY = SERVER
            + "yidao/patient/findDoctorByCategory";

    //新增病人
    public static final String YIDAO_ADD_USER_FOR_PATIENT = SERVER
            + "yidao/user/addUser";

    //用户端，删除副用户
    public static final String YIDAO_USER_DELDEPUTYUSER = SERVER
            + "yidao/patient/delDeputyUser";

    //获取病人列表
    public static final String YIDAO_GET_All_RELATED_USERS = SERVER
            + "yidao/user/getAllRelatedUsers";

    //获取病人列表
    public static final String YIDAO_GET_USER_INFO = SERVER
            + "yidao/patient/getUserInfo";

    //获取默认地址数据
    public static final String YIDAO_GET_DEFAULT_ADDRESS = SERVER
            + "yidao/patient/getDefaultAddress";

    //医生端，获取个人资料页面信息
    public static final String DOCTOR_GET_PROFILE = SERVER
            + "yidao/doctor/getProfile";

    //医生端，重复时间管理页面，获取重复规则
    public static final String GET_REPEAT_TIME_SCHEDULES = SERVER
            + "yidao/doctor/getRepeatTimeSchedules";

    //医生端，编辑重复时间设置规则
    public static final String EDIT_REPEAT_TIME_SCHEDULE = SERVER
            + "yidao/doctor/editRepeatTimeSchedule";

    //获取地址列表
    public static final String YIDAO_GET_ALL_ADDRESS = SERVER
            + "yidao/patient/getAllAddress";

    //设置默认地址
    public static final String YIDAO_SET_DEFAULT_ADDRESS = SERVER
            + "yidao/patient/setDefaultAddress";

    //地址簿中添加新地址
    public static final String YIDAO_SET_ADD_ADDRESS = SERVER
            + "yidao/patient/addAddress";

    //地址簿中修改地址
    public static final String YIDAO_SET_MODIFY_ADDRESS = SERVER
            + "yidao/patient/modifyAddress";

    //删除指定地址
    public static final String YIDAO_DELETE_ADDRESS = SERVER
            + "yidao/patient/deleteAddress";

    //查询所有正在申请的订单
    public static final String GET_ALL_APPLYING_TRX = SERVER
            + "yidao/patient/getAllApplyingTrx";

    //查询所有咨询结束的订单
    public static final String GET_ALL_CLOSED_TRX = SERVER
            + "yidao/patient/getAllClosedTrx";

    //查询所有进行中的订单
    public static final String GET_ALL_ONGOING_TRX = SERVER
            + "yidao/patient/getAllOngoingTrx";

    //医生端, 已完成咨询订单页面
    public static final String CLOSED_TRX_INFO = SERVER
            + "yidao/trx/closedTrxInfo";

    //取消订单
    public static final String CANCEL_TRX = SERVER
            + "yidao/user/cancelTrx";

    //接收订单
    public static final String ACCEPT_APPOINTMENT_REQUEST = SERVER
            + "yidao/trx/acceptAppointmentRequest";

    //拒绝新的预约请
    public static final String REJECT_APPOINTMENT_REQUEST = SERVER
            + "yidao/trx/rejectAppointmentRequest";

    //医生端/客户端, 拒绝修改订单  （老接口）
    public static final String APPROVE_APPOINT_TIME_CHANGE = SERVER
            + "yidao/trx/approveAppointTimeChange";

    //医生端/客户端, 同意修改订单（老接口）
    public static final String REFUSE_APPOINT_TIME_CHANGE = SERVER
            + "yidao/trx/refuseAppointTimeChange";

    //医生端/客户端, 发起修改订单预约时间
    public static final String CHANGE_APPOINTMENT_TIME = SERVER
            + "yidao/trx/changeAppointmentTime";

    //医生端/客户端, 拒绝并发起修改订单预约时间
    public static final String PROPOSE_NEW_APPOINTTIME_CHANGE = SERVER
            + "yidao/trx/proposeNewAppointTimeChange";

    //医生端/客户端, 拒绝并发起修改订单预约时间
    public static final String KEEP_PREVIOUS_TIME = SERVER
            + "yidao/trx/keepPreviousTime";

    //客户端，提交预约请求页面，收费标准提醒
    public static final String GET_CHARGE_TIPS = SERVER
            + "yidao/patient/getChargeTips";

    //获取用户系统默认姓名和电话
    public static final String GET_DEFAULT_INFO = SERVER
            + "yidao/messageBoard/getDefaultInfo";

    //提交用户留言到后台
    public static final String RECORD_NEW_MESSAGE = SERVER
            + "yidao/messageBoard/recordNewMessage";

    //修改用户头像照片(两端通用)
    public static final String USER_CHANGE_PHOTO = SERVER
            + "yidao/user/changePhoto";

    //获取实人认证token
    public static final String GET_GET_ID_AUTH_GET_TOKEN = SERVER
            + "yidao/idAuth/idAuthGetToken";

    // 服务器查询认证结果，如成功则保存证件照和手持证件照到数据库，并更新认证状态
    public static final String GET_ACS_RESPONSE = SERVER
            + "yidao/idAuth/getAcsResponse";

    // 预约前检查用户是否完成了实人认证
    public static final String REQUEST_CHECK = SERVER
            + "yidao/patient/requestCheck";

    //获取OSS访问令牌
    public static final String YIDAO_GET_OSS_ASSUME_ROLE = SERVER
            + "yidao/idAuth/assumeRole";

    /***************** DOCTOR ****************************/

    //医生是否专业认证
    public static final String IS_PROFESSION_AUTH = SERVER
            + "yidao/doctor/isProfessionAuth";

    //获取医生最近两周内的时间安排（包括当天）/ 指定日期的时间安排
    public static final String GET_TIME_ARRANGEMENT = SERVER
            + "yidao/doctor/getTimeArrangement";

    //医生端, 首页 预约请求, 咨询订单, 完成订单入口
    public static final String GET_TRX_STATI_STICINFO = SERVER
            + "yidao/trx/getTrxStatisticInfo";

    //医生添加可预约时间段
    public static final String UPDATE_TIME_SCHEDULE = SERVER
            + "yidao/doctor/updateTimeSchedule";

    //修改某一可预约时间段
    public static final String MODIFY_TIME_ARRANGEMENT = SERVER
            + "yidao/doctor/modifyTimeArrangement";

    //获取新的预约请求
    public static final String GET_NEW_APPOINTMENT_REQUEST = SERVER
            + "yidao/trx/getNewAppointmentRequest";

    //获取医院列表
    public static final String GET_HOSPITAL_LIST = SERVER
            + "yidao/doctor/getHospitalList";

    //根据中文或者拼音首字母查找医院
    public static final String GET_HOSPITAL_LIST_BY_SPELL = SERVER
            + "yidao/doctor/getHospitalListBySpell";


    //医生端, 获取咨询中订单
    public static final String GET_DIAGNOSE_ONGOING_TRX = SERVER
            + "yidao/trx/getDiagnoseOngoingTrx";

    //医生端, 获取待咨询订单
    public static final String GET_WATINGD_IAGNOSING_TRX = SERVER
            + "yidao/trx/getWatingDiagnosingTrx";

    //医生端, 获取已咨询订单
    public static final String GET_DIAGNOSE_COMPLETE_TRX = SERVER
            + "yidao/trx/getDiagnoseCompleteTrx";

    //医生端, 医生端获取二维码
    public static final String GENERATEQR = SERVER
            + "yidao/user/generateQR";

    //客户端扫描医生二维码，老接口优化
    public static final String GET_DOCTOR_INFO_BY_QR = SERVER
            + "yidao/user/getDoctorInfoByQR";

    //“订单开始计时” 接口
    public static final String BEGIN_TIME_KEEPING = SERVER
            + "yidao/patient/beginTimeKeeping";

    //“订单结束计时” 接口
    public static final String STOP_TIME_KEEPING = SERVER
            + "yidao/patient/stopTimeKeeping";

    //医生端, 新的预约请求, 接单前允许三次沟通来了解病情
    public static final String PRE_COMMUNICATION = SERVER
            + "yidao/trx/preCommunication";

    //病人端，查询申请中订单 （老接口，但改了返回，添加了专家和病人互动信息）
    public static final String PRE_COMMUNICATION_REPLY = SERVER
            + "yidao/patient/preCommunicationReply";

    //获取用户证件类型
    public static final String GET_ID_TYPE = SERVER
            + "yidao/user/getIdType";

    //医生端， 计时过程中app被关闭
    public static final String GET_TRX_IN_DIAGNOSING = SERVER
            + "yidao/doctor/getTrxInDiagnosing";

    //客户端， 计时过程中app被关闭
    public static final String GET_TRX_IN_DIAGNOSING_PATIENT = SERVER
            + "yidao/patient/getTrxInDiagnosing";

    //客户端，“咨询结束,病人对医生评价” 接口
    public static final String EVALUATE_SERVICE = SERVER
            + "yidao/patient/evaluateService";

    //医生端, 对病人进行评价
    public static final String EVALUATE_PATIENT = SERVER
            + "yidao/doctor/evaluatePatient";

    //医生端，获取重复时间段
    public static final String GET_REPEAT_TIME_PERIOD_BY_ID = SERVER
            + "yidao/doctor/getRepeatTimePeriodById";

    //医生端 ， 医生填写咨询意见，查询手术方案
    public static final String GET_OPERATION_BY_ICD9 = SERVER
            + "yidao/doctor/getOperationByICD9";

    //医生端 ，医生填写咨询意见，病情确诊
    public static final String GET_DISEASE_BY_ICD10 = SERVER
            + "yidao/doctor/getDiseaseByICD10";

    //医生端，填写咨询意见
    public static final String RECORD_DIAGNOSE_ADVISORY = SERVER
            + "yidao/doctor/recordDiagnoseAdvisory";

    //医生端，订单咨询意见填写完成后, 更新医生的平台虚拟账户金额
    public static final String VACCOUNTPAY = SERVER
            + "yidao/doctor/vAccountPay";

    //医生端，删除当天的该可预约时间段  （只有删除将来所有该可重复时间段才要）
    public static final String INVALID_REPEAT_TIME_SCHEDULE = SERVER
            + "yidao/doctor/invalidRepeatTimeSchedule";

    //医生端，删除当天的该可预约时间段
    public static final String DELETE_TIME_ARRANGEMENT = SERVER
            + "yidao/doctor/deleteTimeArrangement";

    //医生端，删除重复时间规则
    public static final String DELETE_REPEAT_TIME_SCHEDULE = SERVER
            + "yidao/doctor/deleteRepeatTimeSchedule";

    //极光推送，客户端将Registration ID传给服务器
    public static final String SEND_RECORDREGIID = SERVER
            + "yidao/jpush/recordRegiId";

    //客户端】微信支付第一步，统一下单
    public static final String WXPAY_GET_PAY_ORDER = SERVER
            + "yidao/pay/wxpayGetPayOrder";

    //客户端】微信支付第一步，统一下单
    public static final String ALIPAY_GET_PAYORDER = SERVER
            + "yidao/pay/alipayGetPayOrder";

    //获取医生端虚拟账户余额
    public static final String GET_ACCOUNT_AMOUNT = SERVER
            + "yidao/doctor/getAccountAmount";

    //医生端，提现到银行卡
    public static final String WITHDRAW = SERVER
            + "yidao/account/withdraw";

    //医生端，绑定银行借记卡
    public static final String BIND_BANK_ACCOUNT = SERVER
            + "yidao/account/bindBankAccount";

    //用户端，修改手机号
    public static final String CHANGE_PHONENUM = SERVER
            + "yidao/patient/changePhoneNum";

    //医生端，获取银行卡列表
    public static final String GET_CARD_LIST = SERVER
            + "yidao/account/getCardList";

    //绑定银行卡页面，获取用户姓名，打掩码
    public static final String GET_ACCOUNT_NAME = SERVER
            + "yidao/account/getName";

    //客户端，获取优惠券列表
    public static final String GET_DISCOUNT_TICKETS = SERVER
            + "yidao/patient/getDiscountTickets";

    //用户端，支付页面订单信息
    public static final String GET_PAY_TRX_DETAILED_INFO_FOR_PATIENT = SERVER
            + "yidao/patient/getPayTrxDetailedInfoForPatient";

    //客户端，添加疾病历史
    public static final String ADD_DISEASE_HISTORY = SERVER
            + "yidao/patient/addDiseaseHistory";

    //客户端，添加用户过敏药物
    public static final String ADD_ALLERGIC_DRUGS = SERVER
            + "yidao/patient/addAllergicDrugs";

    //意见反馈，医生端获取订单列表信息
    public static final String GET_TRX_INFOD = SERVER
            + "yidao/messageBoard/getTrxInfoD";

    //意见反馈，用户端获取订单列表信息
    public static final String GET_TRX_INFOP = SERVER
            + "yidao/messageBoard/getTrxInfoP";

}
