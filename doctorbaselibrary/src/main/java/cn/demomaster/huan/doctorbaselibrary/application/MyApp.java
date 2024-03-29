package cn.demomaster.huan.doctorbaselibrary.application;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.util.AppStateUtil;
import cn.demomaster.huan.quickdeveloplibrary.ApplicationParent;
import cn.demomaster.huan.quickdeveloplibrary.helper.SoundHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.jpush.android.api.JPushInterface;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

public class MyApp extends ApplicationParent {

    //public static AppSession appSession;
    //private DownLoadCompleteReceiver completeReceiver;
    @Override
    public void onCreate() {
        super.onCreate();

        //appSession = new AppSession();
        //appSession.createSession();

        AppStateUtil.init(this);
        SecurityHelper.initClientKey();

        UserHelper.init(this);

        AppConfig.init(this, "project.conf");
        PopToastUtil.setColorStyle(getResources().getColor(R.color.white), getResources().getColor(R.color.main_color));

        SoundHelper.init(this, true, R.raw.class);//自动加载raw下的音频文件

        //初始化友盟分享
       // initUmengShare("5c79138f61f564e0380012fa");

        UMConfigure.init(this,"5c79138f61f564e0380012fa"
                ,"umeng", UMConfigure.DEVICE_TYPE_PHONE,"");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0

        PlatformConfig.setWeixin("wx611c6c46b8867346", "3baf1193c85774b3fd9d18447d76cab0");
        //豆瓣RENREN平台目前只能在服务器端配置
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
        PlatformConfig.setAlipay("2015111700822536");
        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
        PlatformConfig.setPinterest("1439206");
        PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
        PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
        PlatformConfig.setVKontakte("5764965","5My6SNliAaLxEm3Lyd9J");
        PlatformConfig.setDropbox("oz8v5apet3arcdy","h7p2pjbzkkxt02a");


        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }


}
