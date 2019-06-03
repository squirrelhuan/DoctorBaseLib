package cn.demomaster.huan.doctorbaselibrary.view.activity.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.OrderListActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.setting.*;
import cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient.PatientInfoActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient.PatientListActivity;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.ActionBarInterface;
import cn.demomaster.huan.quickdeveloplibrary.util.AnimationUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    //@BindView(R.id.tv_menu_expert)
    private TextView tv_menu_expert;
    private TextView tv_menu_order;
    private TextView tv_info, tv_share, tv_menu_feedback, tv_help, tv_exit, tv_coupon;

    private ImageView iv_user_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getActionBarLayoutOld().setTitle("");
        //getActionBarLayout().setHeaderBackgroundColor(Color.TRANSPARENT);
        //getActionBarLayout().setBackGroundColor(Color.TRANSPARENT);
        getActionBarLayoutOld().setActionBarModel(ActionBarInterface.ACTIONBAR_TYPE.ACTION_TRANSPARENT);
        // int[] menu_icons = {R.mipmap.ic_menu_expert, R.mipmap.ic_menu_order, R.mipmap.ic_menu_user_info,  R.mipmap.ic_menu_friend, R.mipmap.ic_menu_feedback, R.mipmap.ic_menu_help, R.mipmap.ic_menu_quit};// R.mipmap.ic_menu_setting,R.mipmap.ic_menu_pay,
        // int[] menu_titles = {R.string.menu_expert, R.string.menu_order, R.string.menu_user_info, R.string.menu_friend, R.string.menu_feedback, R.string.menu_help, R.string.menu_quit};//R.string.menu_setting,// R.string.menu_pay,
        // Class[] menu_clazzs = {null, OrderListActivity.class, PatientListActivity.class, ShareActivity.class, SaleServiceActivity.class, HelpCenterActivity.class, HelpCenterActivity.class, HelpCenterActivity.class, HelpCenterActivity.class, LoginActivity.class};//HelpCenterActivity.class,
        tv_exit = findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(this);
        tv_menu_expert = findViewById(R.id.tv_menu_expert);
        tv_menu_expert.setOnClickListener(this);
        tv_menu_order = findViewById(R.id.tv_menu_order);
        tv_menu_order.setOnClickListener(this);
        tv_info = findViewById(R.id.tv_info);
        tv_info.setOnClickListener(this);
        tv_menu_feedback = findViewById(R.id.tv_menu_feedback);
        tv_menu_feedback.setOnClickListener(this);
        tv_help = findViewById(R.id.tv_help);
        tv_help.setOnClickListener(this);
        tv_share = findViewById(R.id.tv_share);
        tv_share.setOnClickListener(this);
        tv_coupon = findViewById(R.id.tv_coupon);
        tv_coupon.setOnClickListener(this);
        iv_user_header = findViewById(R.id.iv_user_header);
        iv_user_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("patientId", UserHelper.getInstance().getCurrentPatient().getUserId());
                startActivity(PatientInfoActivity.class, bundle);
            }
        });
        AnimationUtil.addScaleAnimition(iv_user_header, null);
        TextView tv_userName = findViewById(R.id.tv_userName);
        tv_userName.setText(SessionHelper.getUserName());
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                .skipMemoryCache(false);//不做内存缓存
        if (TextUtils.isEmpty(UserHelper.getInstance().getPrimaryPatient().getPhotoUrl()) || UserHelper.getInstance().getPrimaryPatient().getPhotoUrl().equals("null")) {
            Glide.with(mContext).load(R.mipmap.ic_header_patient).apply(mRequestOptions).into(iv_user_header);
        } else {
            Glide.with(mContext).load(UserHelper.getInstance().getPrimaryPatient().getPhotoUrl()).apply(mRequestOptions).into(iv_user_header);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_menu_expert) {
            finish();
        }
        if (v.getId() == R.id.tv_menu_order) {
            startActivity(OrderListActivity.class);
            //finish();
        }
        if (v.getId() == R.id.tv_info) {
            startActivity(PatientListActivity.class);
            //finish();
        }
        if (v.getId() == R.id.tv_exit) {
            exitApplication();
        }
        if (v.getId() == R.id.tv_menu_feedback) {
            startActivity(SaleServiceActivity.class);
            //finish();
        }
        if (v.getId() == R.id.tv_help) {
            startActivity(HelpCenterActivity.class);
            //finish();
        }
        if (v.getId() == R.id.tv_share) {
            startActivity(ShareActivity.class);
            //finish();
        }
        if (v.getId() == R.id.tv_coupon) {
            startActivity(CouponActivity.class);
            //finish();
        }
    }

}
