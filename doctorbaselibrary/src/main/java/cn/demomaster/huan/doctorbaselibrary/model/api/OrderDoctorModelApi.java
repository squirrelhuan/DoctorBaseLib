package cn.demomaster.huan.doctorbaselibrary.model.api;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/27.
 * description：
 */
public class OrderDoctorModelApi {

    /**
     * name : 褚*庆
     * gender : M
     * age : 26
     * address : 上海市 金山区 悲剧
     * appointmentTime : 2019-01-04 16:15:00
     * descByPatient :
     * descUrlByPatient :
     * trxId : 11
     * additionalInfo : []
     */

    private String requestName;
    private String name;
    private String gender;
    private int age;
    private String address;
    private String appointmentTime;
    private String createdAt;
    private String descByPatient;
    private String descUrlByPatient;
    private String startAt;
    private String endAt;
    private String fee;
    private int trxId;
    private List<AdditionalInfo> additionalInfo;
    private String appointmentTimeChangeCommunication;
    private List<AppointmentTimeChangeCommunication> appointmentTimeChangeCommunicationModel;
    private List<AppointmentTimeChangeModel> appointmentTimeChangeDtoList;
    private int appointmentTimeChangeChance;
    private String phoneNum;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getDescByPatient() {
        return descByPatient;
    }

    public void setDescByPatient(String descByPatient) {
        this.descByPatient = descByPatient;
    }

    public String getDescUrlByPatient() {
        return descUrlByPatient;
    }

    public void setDescUrlByPatient(String descUrlByPatient) {
        this.descUrlByPatient = descUrlByPatient;
    }

    public int getTrxId() {
        return trxId;
    }

    public void setTrxId(int trxId) {
        this.trxId = trxId;
    }

    public List<AdditionalInfo> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(List<AdditionalInfo> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getAppointmentTimeChangeCommunication() {
        return appointmentTimeChangeCommunication;
    }

    public void setAppointmentTimeChangeCommunication(String appointmentTimeChangeCommunication) {
        this.appointmentTimeChangeCommunication = appointmentTimeChangeCommunication;
    }

    public List<AppointmentTimeChangeCommunication> getAppointmentTimeChangeCommunicationModel() {
        if(!TextUtils.isEmpty(appointmentTimeChangeCommunication)){
            appointmentTimeChangeCommunicationModel =  JSON.parseArray(appointmentTimeChangeCommunication,AppointmentTimeChangeCommunication.class);
        }
        return appointmentTimeChangeCommunicationModel;
    }

    public void setAppointmentTimeChangeCommunicationModel(List<AppointmentTimeChangeCommunication> appointmentTimeChangeCommunicationModel) {
        this.appointmentTimeChangeCommunicationModel = appointmentTimeChangeCommunicationModel;
    }

    public List<AppointmentTimeChangeModel> getAppointmentTimeChangeDtoList() {
        return appointmentTimeChangeDtoList;
    }

    public void setAppointmentTimeChangeDtoList(List<AppointmentTimeChangeModel> appointmentTimeChangeDtoList) {
        this.appointmentTimeChangeDtoList = appointmentTimeChangeDtoList;
    }

    public int getAppointmentTimeChangeChance() {
        return appointmentTimeChangeChance;
    }

    public void setAppointmentTimeChangeChance(int appointmentTimeChangeChance) {
        this.appointmentTimeChangeChance = appointmentTimeChangeChance;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public static class AdditionalInfo{
        /**
         * id : 1
         * doctorId : 3
         * patientId : 2
         * requestId : 60
         * sequence : 1
         * question : 嗓子疼吗？
         */

        private int id;
        private int doctorId;
        private int patientId;
        private int requestId;
        private int sequence;
        private String question;
        private String answer;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(int doctorId) {
            this.doctorId = doctorId;
        }

        public int getPatientId() {
            return patientId;
        }

        public void setPatientId(int patientId) {
            this.patientId = patientId;
        }

        public int getRequestId() {
            return requestId;
        }

        public void setRequestId(int requestId) {
            this.requestId = requestId;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }
    }

}
