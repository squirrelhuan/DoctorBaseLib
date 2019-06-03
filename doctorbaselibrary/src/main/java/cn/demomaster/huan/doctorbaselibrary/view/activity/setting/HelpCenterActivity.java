package cn.demomaster.huan.doctorbaselibrary.view.activity.setting;

import android.os.Bundle;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;

public class HelpCenterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        getActionBarLayoutOld().setTitle("帮助中心");
    }
}
