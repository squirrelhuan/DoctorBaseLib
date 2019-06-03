package cn.demomaster.huan.doctorbaselibrary.model.api;

/**
 * @author squirrel桓
 * @date 2018/11/22.
 * description：
 */
public class UserModelApi {


    /**
     * userName : 郑小白
     * userId : 8
     * primary : Y
     */

    private String userName;
    private String userId;
    private String primary;
    private String headUrl;
    private String photoUrl;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
