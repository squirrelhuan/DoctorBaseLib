package cn.demomaster.huan.doctorbaselibrary.view.activity.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.ActionBarInterface;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.GuidePageAdapter;
import cn.demomaster.huan.doctorbaselibrary.util.PermissionManager;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import io.reactivex.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static cn.demomaster.huan.doctorbaselibrary.util.PermissionManager.SYSTEM_ALERT_WINDOW_CODE;


/**
 * 首次进入引导页
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {


    private GuidePageAdapter guidePageAdapter; //适配器
    ViewPager guideViewpager;
    LinearLayout guideLlPoint;
    TextView btnStart;
    RelativeLayout relRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guide);
        getActionBarLayoutOld().setActionBarModel(ActionBarInterface.ACTIONBAR_TYPE.NO_ACTION_BAR);

        super.onCreate(savedInstanceState);

        guideViewpager = findViewById(R.id.guide_viewpager);
        guideLlPoint = findViewById(R.id.guide_ll_point);
        btnStart = findViewById(R.id.btn_start);
        relRoot = findViewById(R.id.rel_root);

        initViews();
    }

    public void initViews() {
        //设置导航栏
        if (getActionBarLayoutOld() != null) {
            getActionBarLayoutOld().setActionBarModel(ActionBarInterface.ACTIONBAR_TYPE.NO_ACTION_BAR);
            //actionBarInterface.hide();
        }

        //加载ViewPager
        initViewPager();
        //加载底部圆点
        initPoint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionManager.getInstance(this).initPermission();
    }

    /**
     * 加载图片ViewPager
     */
    private int[] imageIdArray;//图片资源的数组
    private List<View> viewList;//图片资源的集合

    private void initViewPager() {
        //实例化图片资源
        imageIdArray = new int[]{R.mipmap.splash, R.mipmap.splash, R.mipmap.splash};
        viewList = new ArrayList<>();
        //获取一个Layout参数，设置为全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //循环创建View并加入到集合中
        int len = imageIdArray.length;
        for (int i = 0; i < len; i++) {
            //new ImageView并设置全屏和图片资源
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setImageResource(imageIdArray[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //将ImageView加入到集合中
            viewList.add(imageView);
        }

        guidePageAdapter = new GuidePageAdapter(viewList);
        //View集合初始化好后，设置Adapter
        guideViewpager.setAdapter(guidePageAdapter);
        //设置滑动监听
        guideViewpager.setOnPageChangeListener(this);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("visiable", "hide");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    //实例化原点View
    private ImageView iv_point;
    private ImageView[] ivPointArray;

    /**
     * 加载底部圆点
     */
    private void initPoint() {
        //根据ViewPager的item数量实例化数组
        ivPointArray = new ImageView[viewList.size()];
        //循环新建底部圆点ImageView，将生成的ImageView保存到数组中
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            iv_point = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(25, 25);
            layoutParams.setMargins(20, 0, 20, 0);
            iv_point.setLayoutParams(layoutParams);
            //iv_point.setPadding(0,0,50,0);//left,top,right,bottom
            ivPointArray[i] = iv_point;
            //android:scaleType="fitCenter"
            iv_point.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //第一个页面需要设置为选中状态，这里采用两张不同的图片
            if (i == 0) {
                iv_point.setBackgroundResource(R.mipmap.ic_launcher);//full_holo
            } else {
                iv_point.setBackgroundResource(R.mipmap.ic_launcher);//empty_holo
            }
            //将数组中的ImageView加入到ViewGroup
            guideLlPoint.addView(ivPointArray[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //循环设置当前页的标记图
        int length = imageIdArray.length;
        for (int i = 0; i < length; i++) {
            ivPointArray[position].setBackgroundResource(R.mipmap.ic_launcher);//full_holo
            if (position != i) {
                ivPointArray[i].setBackgroundResource(R.mipmap.ic_launcher);//full_holo
            }
        }

        //判断是否是最后一页，若是则显示按钮
        if (position == imageIdArray.length - 1) {
            btnStart.setVisibility(View.VISIBLE);
        } else {
            btnStart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SYSTEM_ALERT_WINDOW_CODE == requestCode) {
            PermissionManager.getInstance(this).showProcess();
        } else {
            PermissionManager.getInstance(this).initPermission();
        }
    }

    //处理权限申请回调(写在Activity中)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionManager.getInstance(this).initPermission();
    }

}
