package cn.demomaster.huan.doctorbaselibrary.view.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.lang.reflect.Field;

/**
 * 分享页面
 */
public class ShareActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        TextView tv_001 = findViewById(R.id.tv_001);
        TextView tv_content = findViewById(R.id.tv_content);


        //http://www.drvisit.com.cn/share.html
        final UMShareListener umShareListener = new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        };

        boolean isPatient = AppConfig.getInstance().isPatient();
        if (isPatient) {
            tv_001.setText("获得抵用金");
            tv_content.setText("1. 奖励一：已完成实名认证的主注册用户每邀请一位好友注册专家上门app成功，邀请人可获得抵用金200元。\n\n" +
                    "2. 奖励二：被邀请的好友完成实名认证成功，邀请人可再获得抵用金300元");
        } else {
            tv_001.setText("获得现金");
            tv_content.setText("1. 奖励一：已完成实名认证的专家每邀请一位主任医师注册专家上门app成功，邀请人可获得现金奖励150元；每邀请一位副主任医师注册成功，邀请人可获得现金奖励100元；每邀请一位主治医师注册成功，邀请人可获得现金奖励50元。\n\n" +
                    "2. 奖励二：被邀请的主任医师完成实名认证，邀请人可再获得现金奖励150元；被邀请的副主任医师完成实名认证，邀请人可再获得现金奖励100元；被邀请的主治医师完成实名认证，邀请人可再获得现金奖励50元。");
        }
        final int id = getResId( isPatient?"ic_launcher_patient":"ic_launcher_doctor", R.mipmap.class);
        getActionBarLayoutOld().setTitle("邀请有礼");
        final String url = "http://www.drvisit.com.cn/share.html?sharerId="+ (isPatient?UserHelper.getInstance().getPrimaryPatient().getUserId():"0") +"&Tag=" + (isPatient ? "P" : "D");
        TextView tv_share = findViewById(R.id.tv_share);
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UMImage image = new UMImage(ShareActivity.this,id);//资源文件
              /*  new ShareAction(mContext).withText(""+url).withMedia(image)
                        .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                        .setCallback(umShareListener).open();
*/
                UMWeb  web = new UMWeb(url);
                web.setTitle("专家上门");//标题
                web.setThumb(image);  //缩略图
                web.setDescription("手机预约医生专家，实现病人和医生面对面的便捷居家医疗咨询");//描述

                new ShareAction(ShareActivity.this)
                        .withMedia(web)
                        .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                        .open();
            }
        });

    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
