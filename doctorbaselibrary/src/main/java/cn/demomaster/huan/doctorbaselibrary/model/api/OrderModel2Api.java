package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.io.Serializable;

/**
 * @author squirrel桓
 * @date 2018/11/27.
 * description：
 */
public class OrderModel2Api implements Serializable {

    /**
     * trxId : 85
     * trxName : 全科 20190415130156
     * patientName : 褚国庆
     * appointTime : 2019-04-15 13:15:00
     * status : ACCEPTED
     */

    private int trxId;
    private String trxName;
    private String patientName;
    private String appointTime;
    private String doctorName;
    private String title;
    private String status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public int getTrxId() {
        return trxId;
    }

    public void setTrxId(int trxId) {
        this.trxId = trxId;
    }

    public String getTrxName() {
        return trxName;
    }

    public void setTrxName(String trxName) {
        this.trxName = trxName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(String appointTime) {
        this.appointTime = appointTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
