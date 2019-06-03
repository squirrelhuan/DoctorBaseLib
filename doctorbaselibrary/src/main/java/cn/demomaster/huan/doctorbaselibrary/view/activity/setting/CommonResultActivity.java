package cn.demomaster.huan.doctorbaselibrary.view.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.OrderListActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.ActivityManager;

public class CommonResultActivity extends BaseActivity {

    private ImageView iv_result;
    private TextView tv_message, tv_know;

    private String title,message,button_message;
    private int resultResId = R.mipmap.ic_result_success;
    private boolean isOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_result);


        iv_result = findViewById(R.id.iv_result);
        tv_message = findViewById(R.id.tv_message);
        tv_know = findViewById(R.id.tv_know);
        tv_know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            if (mBundle.containsKey("title")) {
                title = mBundle.getString("title");
                getActionBarLayoutOld().setTitle(title);
            }
            if (mBundle.containsKey("message")) {
                message = mBundle.getString("message");
                tv_message.setText(message);
            }
            if (mBundle.containsKey("button_message")) {
                button_message = mBundle.getString("button_message");
                tv_know.setText(button_message);
            }
            if (mBundle.containsKey("resultResId")) {
                resultResId = mBundle.getInt("resultResId");
                iv_result.setImageResource(resultResId);
            }
            if (mBundle.containsKey("isOrder")) {
                isOrder = mBundle.getBoolean("isOrder");
            }
        }
        if(isOrder){
            getActionBarLayoutOld().setLeftOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToOrderList();
                }
            });
            tv_know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToOrderList();
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isOrder){
            goToOrderList();
        }
    }

    public void goToOrderList(){
        ActivityManager.getInstance().deleteOtherActivityByClass(AppConfig.getInstance().getMainActivityActivity());
        startActivity(OrderListActivity.class);
    }

}
