package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.io.Serializable;

/**
 * @author squirrel桓
 * @date 2018/11/20.
 * description：
 */
public class DoctorModelApi implements Serializable {


    /**
     * doctorId : 12
     * title : 3
     * hospital : 长海医院
     * domain : aa
     * specialDomainDesc : 擅长全科治疗
     * overallMerit : 3
     * professionMerit : 4
     * serviceMerit : 5
     * qualificationCerPhotoUrl : www.1.com
     * photoUrl : http://www.yibaicdn.com/FhOqs-qqPnCm8pGa3uwQGmUH7WMU
     * doctorName : 阮经天
     */

    private String doctorId;
    private String title;
    private String hospital;
    private String domain;
    private String specialDomainDesc;
    private int overallMerit;
    private int professionMerit;
    private int serviceMerit;
    private String qualificationCerPhotoUrl;
    private String photoUrl;
    private String doctorName;
    private String language;
    private int evaluationNum;

    public int getEvaluationNum() {
        return evaluationNum;
    }

    public void setEvaluationNum(int evaluationNum) {
        this.evaluationNum = evaluationNum;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSpecialDomainDesc() {
        return specialDomainDesc;
    }

    public void setSpecialDomainDesc(String specialDomainDesc) {
        this.specialDomainDesc = specialDomainDesc;
    }

    public int getOverallMerit() {
        return overallMerit;
    }

    public void setOverallMerit(int overallMerit) {
        this.overallMerit = overallMerit;
    }

    public int getProfessionMerit() {
        return professionMerit;
    }

    public void setProfessionMerit(int professionMerit) {
        this.professionMerit = professionMerit;
    }

    public int getServiceMerit() {
        return serviceMerit;
    }

    public void setServiceMerit(int serviceMerit) {
        this.serviceMerit = serviceMerit;
    }

    public String getQualificationCerPhotoUrl() {
        return qualificationCerPhotoUrl;
    }

    public void setQualificationCerPhotoUrl(String qualificationCerPhotoUrl) {
        this.qualificationCerPhotoUrl = qualificationCerPhotoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
