package cn.demomaster.huan.doctorbaselib;

import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.util.AppStateUtil;
import cn.demomaster.huan.quickdeveloplibrary.ApplicationParent;

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

        AppConfig.init(this,"project.conf");

    }



}
