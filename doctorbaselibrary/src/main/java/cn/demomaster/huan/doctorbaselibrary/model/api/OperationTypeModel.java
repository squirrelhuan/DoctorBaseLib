package cn.demomaster.huan.doctorbaselibrary.model.api;

import java.io.Serializable;

/**
 * 手术类型，用于诊断结果
 * @author squirrel桓
 * @date 2019/2/27.
 * description：
 */
public class OperationTypeModel implements Serializable {

    /**
     * id : 100
     * code : 00.66004
     * operationName : 经皮冠状动脉球囊扩张成形术
     * spell : jpgzdmqnkzcxs
     */

    private String id;
    private String code;
    private String operationName;
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

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }
}
