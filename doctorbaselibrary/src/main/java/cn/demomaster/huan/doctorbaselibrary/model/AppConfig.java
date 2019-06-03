package cn.demomaster.huan.doctorbaselibrary.model;

import android.content.Context;
import android.util.Log;
import cn.demomaster.huan.doctorbaselibrary.util.FileUtil;
import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * @author squirrel桓
 * @date 2018/12/10.
 * description：
 */
public class AppConfig {

    private static String configPath;
    private static Context mContext;
    private static AppConfig instance;
    private static Map<String, Object> map;

    //要初始化
    public static void init(Context context, String pathName) {
        mContext = context;
        configPath = pathName;
    }

    private AppConfig() {
        String conf = FileUtil.getFromAssets(mContext, configPath);
        map = JSON.parseObject(conf, Map.class);

       /* String conf = FileUtil.getFromAssets(mContext, "project.conf");

        Map<String,Object> map =new HashMap<>();
        map.put("isPatient",true);
        map.put("version",1);
        String cof =  JSON.toJSONString(map);
        Log.i(TAG,cof);
        PopToastUtil.ShowToast(mContext,conf);*/

    }

    public static AppConfig getInstance() {
        if (mContext == null) {
            Log.e("CGQ", "AppConfig 未初始化");
            return null;
        }
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public boolean isPatient() {
        return (boolean) map.get("isPatient");
    }

    public static Class getAuthorizationActivity() {
        String className = (String) map.get("AuthorizationActivity");
        return getClassByClassName(className);
    }

    public static Class getMainActivityActivity() {
        String className = (String) map.get("MainActivity");
        return getClassByClassName(className);
    }
    public static Class getOrderPayActivity() {
        String className = (String) map.get("OrderPayActivity");
        return getClassByClassName(className);
    }
    public static Class getAdviceAndSuggestionsActivity() {
        String className = (String) map.get("AdviceAndSuggestionsActivity");
        return getClassByClassName(className);
    }


    public static Class getClassByClassName(String className) {
        Class catClass = null; // 根据给定的类名初始化类
        try {
            catClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return catClass;
    }

}
