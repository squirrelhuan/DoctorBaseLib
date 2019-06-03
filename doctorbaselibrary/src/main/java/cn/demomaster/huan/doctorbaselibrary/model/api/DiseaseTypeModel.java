package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.io.Serializable;

/**
 * 疾病类型，用于诊断结果
 * @author squirrel桓
 * @date 2019/2/27.
 * description：
 */
public class DiseaseTypeModel implements Serializable {

    /**
     * id : 10029
     * code : M89.301
     * disease : 颧骨肥大
     * spell : qgfd
     */

    private String id;
    private String code;
    private String disease;
    private String spell;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }
}
