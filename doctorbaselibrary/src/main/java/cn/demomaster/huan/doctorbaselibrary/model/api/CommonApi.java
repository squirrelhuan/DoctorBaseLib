package cn.demomaster.huan.doctorbaselibrary.model.api;

/**
 * 接口通用数据模型
 * Created by Squirrel桓
 */
public class CommonApi {
    private int retCode;
    private Object data;
    private String message;

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
