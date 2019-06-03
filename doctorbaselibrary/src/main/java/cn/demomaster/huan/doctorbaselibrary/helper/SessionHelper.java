package cn.demomaster.huan.doctorbaselibrary.helper;

import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;

/**
 * @author squirrel桓
 * @date 2018/11/13.
 * description：
 */
public class SessionHelper {


    public static String Share_Session_Client_PublicKey="Share_Session_Client_PublicKey";
    public static String Share_Session_Client_PrivateKey="Share_Session_Client_PrivateKey";
    public static String Share_Session_Server_PublicKey="Share_Session_Server_PublicKey";
    public static String Share_Session_SessionId="Share_Session_SessionId";
    public static String Share_Session_Token="Share_Session_Token";
    public static String Share_Session_UserName="Share_Session_UserName";
    public static String Share_Session_Uuid="Share_Session_Uuid";
    public static String getServerPublicKey(){
       return SharedPreferencesHelper.getInstance().getString(Share_Session_Server_PublicKey,null);
    }

    public static void setServerPublicKey(String publicKey){
        SharedPreferencesHelper.getInstance().putString(Share_Session_Server_PublicKey,publicKey);
    }

    public static String getSessionId(){
        return SharedPreferencesHelper.getInstance().getString(Share_Session_SessionId,null);
    }

    public static void setSessionId(String publicKey){
        SharedPreferencesHelper.getInstance().putString(Share_Session_SessionId,publicKey);
    }

    public static String getToken(){
        return SharedPreferencesHelper.getInstance().getString(Share_Session_Token,null);
    }

    public static void setToken(String toketn){
        SharedPreferencesHelper.getInstance().putString(Share_Session_Token,toketn);
    }

    public static String getUserName(){
        return SharedPreferencesHelper.getInstance().getString(Share_Session_UserName,null);
    }

    public static void setUserName(String userName){
        SharedPreferencesHelper.getInstance().putString(Share_Session_UserName,userName);
    }

    public static String getUuid(){
        return SharedPreferencesHelper.getInstance().getString(Share_Session_Uuid,null);
    }

    public static void setUuid(String uuid){
        SharedPreferencesHelper.getInstance().putString(Share_Session_Uuid,uuid);
    }













    //客户端
    public static String getClientPublickey(){
        return SharedPreferencesHelper.getInstance().getString(Share_Session_Client_PublicKey,null);
    }

    public static void setClientPublickey(String publickey){
        SharedPreferencesHelper.getInstance().putString(Share_Session_Client_PublicKey,publickey);
    }


    public static String getClientPrivatekey(){
        return SharedPreferencesHelper.getInstance().getString(Share_Session_Client_PrivateKey,null);
    }

    public static void setClientPrivatekey(String privatekey){
        SharedPreferencesHelper.getInstance().putString(Share_Session_Client_PrivateKey,privatekey);
    }


}
