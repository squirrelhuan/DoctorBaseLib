package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.sql.Time;
import java.util.Date;

/**
 * @author squirrel桓
 * @date 2018/12/13.
 * description：
 */
public class NotificationModel {
    private String id;
    private String message;
    private String time;
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
