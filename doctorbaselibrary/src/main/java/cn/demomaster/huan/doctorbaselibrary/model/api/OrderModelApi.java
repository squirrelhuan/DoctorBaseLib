package cn.demomaster.huan.doctorbaselibrary.model.api;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/27.
 * description：
 */
public class OrderModelApi implements Serializable {


    /**
     * patientName : 褚国庆
     * requestName : 头颈外科_20181120114436
     * requestId : 17
     * waitingTime : 6天22小时49分钟37秒673毫秒
     * additionalInfo : []
     */

    private String patientName;
    private String requestName;
    private String doctorName;
    private String requestId;
    private String waitingTime;
    private String requestStatus;
    private String appointTime;
    private List<OrderDoctorModelApi.AdditionalInfo> additionalInfo;
    private String appointmentTimeChangeCommunication;
    private List<AppointmentTimeChangeCommunication> appointmentTimeChangeCommunicationModel;
    private int appointmentTimeChangeChance;//appointmentTimeChangeChance，0就表示不要显示修改时间按钮，1表示显示
    private String isEvaluated;//"N"没有评价

    public String getIsEvaluated() {
        return isEvaluated;
    }

    public void setIsEvaluated(String isEvaluated) {
        this.isEvaluated = isEvaluated;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }

    public List<OrderDoctorModelApi.AdditionalInfo> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(List<OrderDoctorModelApi.AdditionalInfo> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public String getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(String appointTime) {
        this.appointTime = appointTime;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
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

    public int getAppointmentTimeChangeChance() {
        return appointmentTimeChangeChance;
    }

    public void setAppointmentTimeChangeChance(int appointmentTimeChangeChance) {
        this.appointmentTimeChangeChance = appointmentTimeChangeChance;
    }
}
