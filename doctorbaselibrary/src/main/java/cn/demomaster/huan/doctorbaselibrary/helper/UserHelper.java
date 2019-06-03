package cn.demomaster.huan.doctorbaselibrary.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.demomaster.huan.doctorbaselibrary.model.api.DoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.DoctorModelApi_D;
import com.alibaba.fastjson.JSON;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.UserModelApi;

import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;

/**
 * @author squirrel桓
 * @date 2018/12/5.
 * description：
 */
public class UserHelper {
    private String shareprefrence_doctorModelApi="shareprefrence_doctorModelApi";
    private String shareprefrence_primaryPatient="shareprefrence_primaryPatient";
    private String shareprefrence_currentPatient="shareprefrence_currentPatient";
    private String shareprefrence_addRessModel="shareprefrence_addRessModel";
    private String shareprefrence_dateTime="shareprefrence_dateTime";
    private UserModelApi currentPatient;
    private UserModelApi primaryPatient;
    private DoctorModelApi_D doctorModelApi;

    private AddRessModel addRessModel;
    private String dateTime;
    private SharedPreferences sharedPreferences;
    private static UserHelper instance;

    public static UserHelper getInstance() {
        if(instance==null){
            instance = new UserHelper();
        }
        return instance;
    }

    public UserHelper() {

    }

    public static void init(Context context){
        getInstance().sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public UserModelApi getCurrentPatient() {
        return (UserModelApi) getModel(shareprefrence_currentPatient,UserModelApi.class);
    }

    public void setCurrentPatient(UserModelApi currentPatient) {
        this.currentPatient = currentPatient;
        SharedPreferencesHelper.getInstance().putString(shareprefrence_currentPatient,JSON.toJSONString(currentPatient));
    }

    public UserModelApi getPrimaryPatient() {
        return (UserModelApi) getModel(shareprefrence_primaryPatient,UserModelApi.class);
    }

    public void setPrimaryPatient(UserModelApi primaryPatient) {
        this.primaryPatient = primaryPatient;
        SharedPreferencesHelper.getInstance().putString(shareprefrence_primaryPatient,JSON.toJSONString(primaryPatient));
    }

    public DoctorModelApi_D getDoctorModelApi() {
        return  (DoctorModelApi_D) getModel(shareprefrence_doctorModelApi,DoctorModelApi_D.class);
    }

    public void setDoctorModelApi(DoctorModelApi_D doctorModelApi) {
        this.doctorModelApi = doctorModelApi;
        SharedPreferencesHelper.getInstance().putString(shareprefrence_doctorModelApi,JSON.toJSONString(doctorModelApi));
    }

    public AddRessModel getAddRessModel() {
        return (AddRessModel) getModel(shareprefrence_addRessModel,AddRessModel.class);
    }

    public void setAddRessModel(AddRessModel addRessModel) {
        this.addRessModel = addRessModel;
        SharedPreferencesHelper.getInstance().putString(shareprefrence_addRessModel,JSON.toJSONString(addRessModel));
    }

    public String getDateTime() {
        return (String) getModel(shareprefrence_dateTime,String.class);
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
        SharedPreferencesHelper.getInstance().putString(shareprefrence_dateTime,JSON.toJSONString(dateTime));
    }

    private Object getModel(String shareprefrence_currentPatient,Class clazz) {
        if(sharedPreferences.contains(shareprefrence_currentPatient)){
            return JSON.parseObject(SharedPreferencesHelper.getInstance().getString(shareprefrence_currentPatient,null),clazz);
        }else {
            return null;
        }
    }
}
