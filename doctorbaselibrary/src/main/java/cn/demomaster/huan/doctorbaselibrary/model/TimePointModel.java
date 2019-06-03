package cn.demomaster.huan.doctorbaselibrary.model;

/**
 * @author squirrel桓
 * @date 2019/1/18.
 * description：空闲时间点
 */
public class TimePointModel {


    /**
     * scheduleDate : 2019-01-23
     * availableTime : 10:00,10:15,10:30,10:45,11:00,11:15,11:30,11:45,12:00,12:15,12:30,12:45,13:00
     */

    private String scheduleDate;
    private String availableTime;

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }
}
