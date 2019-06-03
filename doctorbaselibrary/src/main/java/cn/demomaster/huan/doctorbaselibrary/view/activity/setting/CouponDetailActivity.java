package cn.demomaster.huan.doctorbaselibrary.view.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.CouponModelApi;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;

public class CouponDetailActivity extends BaseActivity {

    private CouponModelApi couponModelApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            couponModelApi = (CouponModelApi) bundle.getSerializable("coupon");
        }
        init();
    }
    TextView tv_coupon_title,tv_coupon_amount,tv_coupon_unit,tv_coupon_time,tv_use_coupon;
    private void init() {
        if(couponModelApi==null)return;
        tv_coupon_title = findViewById(R.id.tv_coupon_title);
        tv_coupon_amount = findViewById(R.id.tv_coupon_amount);
        tv_coupon_unit = findViewById(R.id.tv_coupon_unit);
        tv_coupon_time = findViewById(R.id.tv_coupon_time);
        tv_use_coupon = findViewById(R.id.tv_use_coupon);
        tv_use_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle  = new Bundle();
                bundle.putSerializable("coupon",couponModelApi);
                intent.putExtras(bundle);
                setResult(200,intent);
                finish();
            }
        });

        tv_coupon_title.setText(couponModelApi.getType().equals("DEDUCTION")?"抵用券":"折扣券");//DEDUCTION","抵用券/ DISCOUNT折扣券
        tv_coupon_amount.setText(couponModelApi.getAmount()+"");
        tv_coupon_unit.setText(couponModelApi.getType().equals("DEDUCTION")?"元":"折");
        tv_coupon_time.setText(couponModelApi.getExpireAt()+"");
    }


}
