package cn.demomaster.huan.doctorbaselibrary.model;

import java.io.Serializable;

/**
 * @author squirrel桓
 * @date 2019/2/20.
 * description：
 */
public class TimerSpan implements Serializable {

    /**
     * requestName : 全科_20190130170227
     * timeSpan : -1791571526
     * trxId : 54
     */

    private String requestName;
    private String timeSpan;
    private int trxId;

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }

    public int getTrxId() {
        return trxId;
    }

    public void setTrxId(int trxId) {
        this.trxId = trxId;
    }
}
