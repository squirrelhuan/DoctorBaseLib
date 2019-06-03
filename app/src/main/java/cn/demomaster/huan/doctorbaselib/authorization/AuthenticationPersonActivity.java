package cn.demomaster.huan.doctorbaselib.authorization;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.security.rp.RPSDK;

import cn.demomaster.huan.doctorbaselib.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.R;

/**
 * 实人认证
 */
public class AuthenticationPersonActivity extends BaseActivity {

    TextView tvNext;
    LinearLayout ll01;
    TextView tvBack;
    LinearLayout ll02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_person);

        RPSDK.initialize(this);
        getActionBarLayout().setTitle(" 实人认证");

        init();
    }

    private void init() {
        tvNext = findViewById(R.id.tv_next);
        ll01 = findViewById(R.id.ll_01);
        tvBack = findViewById(R.id.tv_back);
        ll02 = findViewById(R.id.ll_02);
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll01.setVisibility(View.GONE);
                ll02.setVisibility(View.VISIBLE);
            }
        });
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll02.setVisibility(View.GONE);
                ll01.setVisibility(View.VISIBLE);
            }
        });
    }
}
