package cn.demomaster.huan.doctorbaselibrary.model;

import java.io.Serializable;

/**
 * @author squirrel桓
 * @date 2018/11/19.
 * description：
 */
public class DepartMentModel implements Serializable {

    private String name;
    private int type;
    private String iconName;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
