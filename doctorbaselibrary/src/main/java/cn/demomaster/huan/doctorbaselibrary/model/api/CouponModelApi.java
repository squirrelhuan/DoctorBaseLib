package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.io.Serializable;

/**
 * @author squirrel桓
 * @date 2019/5/8.
 * description：
 */
public class CouponModelApi implements Serializable {

    /**
     * id : 1
     * type : DEDUCTION
     * amount : 200.0
     * expireAt : 2020-05-08 23:59
     * multipleUsage : F
     */

    private String id;
    private String type;
    private double amount;
    private String expireAt;
    private String multipleUsage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }

    public String getMultipleUsage() {
        return multipleUsage;
    }

    public void setMultipleUsage(String multipleUsage) {
        this.multipleUsage = multipleUsage;
    }
}
