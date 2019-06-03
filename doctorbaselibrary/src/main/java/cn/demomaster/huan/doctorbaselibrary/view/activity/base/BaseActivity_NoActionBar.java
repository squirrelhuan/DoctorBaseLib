package cn.demomaster.huan.doctorbaselibrary.view.activity.base;

import android.graphics.Color;
import android.os.Bundle;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.OptionsMenu;
import cn.demomaster.huan.quickdeveloplibrary.util.StatusBarUtil;
import cn.demomaster.huan.doctorbaselibrary.util.ActionBarUtil;

import static cn.demomaster.huan.doctorbaselibrary.util.ActionBarUtil.*;

public class BaseActivity_NoActionBar extends BaseActivity implements BaseActivityInterface {

    //状态栏模式
    public int StatusBarDarkMode = 0;
    //导航栏
    public ActionBarInterface actionBarInterface;

    @Override
    public boolean isUseActionBarLayout() {
        return false;//不使用qiuck导航栏
    }
    @Override
    public int getHeadlayoutResID() {
        return R.layout.activity_actionbar_common_header;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentBar();

    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        StatusBarDarkMode = StatusBarUtil.StatusBarLightMode(this);
        //初始化导航栏
        initActionBar();

        //初始化数据
        initData();
        //初始化视图
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initData() {
        //TODO 初始化数据
    }

    @Override
    public void initViews() {
        //TODO 初始化视图
    }

    public void initActionBar() {
        //设置通用导航栏
        actionBarInterface = ActionBarUtil.getActionBar(this, ActionBarCommon, true);
        //设置导航栏
        if (actionBarInterface != null && mBundle != null ) {
            if(mBundle.containsKey(COMMON_ACTIONBAR_TITLE)) {
                actionBarInterface.setTitle(mBundle.getString(COMMON_ACTIONBAR_TITLE, ""));
            }
            if (mBundle.containsKey(COMMON_ACTIONBAR_NOACTIONBAR)) {
                boolean NoActionBar = mBundle.getBoolean(COMMON_ACTIONBAR_NOACTIONBAR);
                if (NoActionBar) {
                    actionBarInterface.hide();
                }else {
                    //
                }
            }else {
                actionBarInterface.hide();
            }
        }
    }

    private OptionsMenu.Builder optionsMenubuilder;
    //获取自定义菜单
    public OptionsMenu.Builder getOptionsMenuBuilder() {
        if (optionsMenubuilder == null) {
            optionsMenubuilder = new OptionsMenu.Builder(mContext);
        }
        return optionsMenubuilder;
    }
}
