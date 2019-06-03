package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.io.Serializable;

/**
 * @author squirrel桓
 * @date 2019/1/15.
 * description：
 */
public class DoctorModelApi_D implements Serializable {

    /**
     * isAuth : Y
     * name : 王茹
     * title : 1
     * photo : http://drvisit-photo.oss-cn-hangzhou.aliyuncs.com/drvisit-photo-%E7%8E%8B%E8%8C%B9-1546846043837
     */

    private String isAuth;
    private String isIdentityAuth;//实人认证
    private String name;
    private String title;
    private String photo;
    private String personalImageUrl;
    private String lanuage;
    private String speciatlCategoryDesc;
    private String category;
    private String hospital;
    private String phoneNumber;
    private String qualificationCerPhotoUrl;
    private String qualificationCerPhotoUrl1;
    private String qualificationCerPhotoUrl2;

    public String getIsIdentityAuth() {
        return isIdentityAuth;
    }

    public void setIsIdentityAuth(String isIdentityAuth) {
        this.isIdentityAuth = isIdentityAuth;
    }

    public String getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(String isAuth) {
        this.isAuth = isAuth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPersonalImageUrl() {
        return personalImageUrl;
    }

    public void setPersonalImageUrl(String personalImageUrl) {
        this.personalImageUrl = personalImageUrl;
    }

    public String getLanuage() {
        return lanuage;
    }

    public void setLanuage(String lanuage) {
        this.lanuage = lanuage;
    }

    public String getSpeciatlCategoryDesc() {
        return speciatlCategoryDesc;
    }

    public void setSpeciatlCategoryDesc(String speciatlCategoryDesc) {
        this.speciatlCategoryDesc = speciatlCategoryDesc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getQualificationCerPhotoUrl() {
        return qualificationCerPhotoUrl;
    }

    public void setQualificationCerPhotoUrl(String qualificationCerPhotoUrl) {
        this.qualificationCerPhotoUrl = qualificationCerPhotoUrl;
    }

    public String getQualificationCerPhotoUrl1() {
        return qualificationCerPhotoUrl1;
    }

    public void setQualificationCerPhotoUrl1(String qualificationCerPhotoUrl1) {
        this.qualificationCerPhotoUrl1 = qualificationCerPhotoUrl1;
    }

    public String getQualificationCerPhotoUrl2() {
        return qualificationCerPhotoUrl2;
    }

    public void setQualificationCerPhotoUrl2(String qualificationCerPhotoUrl2) {
        this.qualificationCerPhotoUrl2 = qualificationCerPhotoUrl2;
    }
}
