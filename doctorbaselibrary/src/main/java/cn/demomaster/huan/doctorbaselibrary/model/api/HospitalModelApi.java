package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.io.Serializable;

/**
 * @author squirrel桓
 * @date 2018/12/25.
 * description：
 */
public class HospitalModelApi implements Serializable {

    /**
     * hospitalName : 上海市第二人民医院
     * rate : 2_A
     * hospitalCode : sh_19
     * spell : shsdermyy
     */

    private String hospitalName;
    private String rate;
    private String hospitalCode;
    private String spell;

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getHospitalCode() {
        return hospitalCode;
    }

    public void setHospitalCode(String hospitalCode) {
        this.hospitalCode = hospitalCode;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }
}
