package cn.demomaster.huan.doctorbaselibrary.model;

/**
 * @author squirrel桓
 * @date 2018/11/16.
 * description：
 */
public class MainMenu {

    private int iconResId;
    private int titleResId;
    private Class targetClszz;

    public MainMenu(int iconResId, int titleResId, Class targetClszz) {
        this.iconResId = iconResId;
        this.titleResId = titleResId;
        this.targetClszz = targetClszz;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public void setTitleResId(int titleResId) {
        this.titleResId = titleResId;
    }

    public Class getTargetClszz() {
        return targetClszz;
    }

    public void setTargetClszz(Class targetClszz) {
        this.targetClszz = targetClszz;
    }
}
