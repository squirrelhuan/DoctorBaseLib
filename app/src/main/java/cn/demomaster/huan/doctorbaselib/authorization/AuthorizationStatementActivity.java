package cn.demomaster.huan.doctorbaselib.authorization;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.demomaster.huan.doctorbaselib.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.R;

/**
 * 授权声明
 */
public class AuthorizationStatementActivity extends BaseActivity {

    TextView tvAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_statement);

        getActionBarLayout().setTitle("受权声明");

        init();
    }

    private void init() {
        tvAccept=findViewById(R.id.tv_accept);
        tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AuthenticationPersonActivity.class);
            }
        });
    }
}
