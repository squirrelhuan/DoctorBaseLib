package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.io.Serializable;

/**
 * @author squirrel桓
 * @date 2019/5/8.
 * description：
 */
public class RepeatTimeModelApi implements Serializable {

    /**
     * id : 4
     * repeatType : everyday
     * startAt : 10:00
     * endAt : 13:00
     * repeatValue :
     * expireDate : 99999999
     * takeEffectDay : 20190120
     */

    private String id;
    private String repeatType;
    private String startAt;
    private String endAt;
    private String repeatValue;
    private String expireDate;//失效日期
    private String takeEffectDay;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
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

    public String getRepeatValue() {
        return repeatValue;
    }

    public void setRepeatValue(String repeatValue) {
        this.repeatValue = repeatValue;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getTakeEffectDay() {
        return takeEffectDay;
    }

    public void setTakeEffectDay(String takeEffectDay) {
        this.takeEffectDay = takeEffectDay;
    }
}
