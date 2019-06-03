package cn.demomaster.huan.doctorbaselibrary.model.api;

/**
 * @author squirrel桓
 * @date 2018/12/24.
 * description：
 */
public class ChargeTipModelApi {


    /**
     * baseFee : 1800
     * extraFee : 56
     * titleCode : 1
     * titleName : 主任医师
     */

    private String baseFee;
    private String extraFee;
    private String titleCode;
    private String titleName;

    public String getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(String baseFee) {
        this.baseFee = baseFee;
    }

    public String getExtraFee() {
        return extraFee;
    }

    public void setExtraFee(String extraFee) {
        this.extraFee = extraFee;
    }

    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(String titleCode) {
        this.titleCode = titleCode;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
}
