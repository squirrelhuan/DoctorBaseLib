package cn.demomaster.huan.doctorbaselibrary.securit;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import cn.demomaster.huan.doctorbaselibrary.application.RSAServiceImpl;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;

import java.util.Map;


public class SecurityHelper {



    public static String encryptData(String data, String serverPublicKey) {
        String str = null;
        try {
            Map map = RSAServiceImpl.encryptData(data, serverPublicKey);
            str = JSON.toJSONString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    //加密
    public static String jsSignDouble(String data1, String data2) {
        String str = null;
        try {
            Map map = RSAServiceImpl.encryptDubleData(data1, data2, SessionHelper.getServerPublicKey());
            str = JSON.toJSONString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    //加密
    public static String jsSign(String data, Boolean useUUID) {
        String str = null;
        try {
            if (useUUID) {
                Map map = RSAServiceImpl.encryptData(data, SessionHelper.getServerPublicKey());// MyApp.appSession.getServerPublicKey());
                str = JSON.toJSONString(map);
            } else {
                byte[] bytes = RSAServiceImpl.encryptByPublicKey(data, SessionHelper.getServerPublicKey());//, MyApp.appSession.getServerPublicKey());
                str = RSAServiceImpl.encryptBASE64(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

                            //RSAServiceImpl.decryptData(token, uuid, SessionHelper.getClientPrivatekey());
    //解密
    public static String decryptData(String targetStr, String uuid, String clientPrivateKey) {
        String ret = "";
        try {
            ret = RSAServiceImpl.decryptData(targetStr, uuid, clientPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /*初始化客户端公钥私钥*/
    public static void initClientKey() {
        String clientPublicKey = SessionHelper.getClientPublickey();
        String clientPrivateKey = SessionHelper.getClientPrivatekey();
        if (TextUtils.isEmpty(clientPublicKey) || TextUtils.isEmpty(clientPrivateKey)) {
            Map keyMap = RSAServiceImpl.initKey();
            clientPublicKey = RSAServiceImpl.getPublicKey(keyMap);
            clientPrivateKey = RSAServiceImpl.getPrivateKey(keyMap);
            SessionHelper.setClientPublickey(clientPublicKey);
            SessionHelper.setClientPrivatekey(clientPrivateKey);
        }
    }


    public static class RsaModel {
        private String encryptData1;
        private String encryptData2;
        private String encryptData;
        private String uuid;

        public String getEncryptData() {
            return encryptData;
        }

        public void setEncryptData(String encryptData) {
            this.encryptData = encryptData;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getEncryptData1() {
            return encryptData1;
        }

        public void setEncryptData1(String encryptData1) {
            this.encryptData1 = encryptData1;
        }

        public String getEncryptData2() {
            return encryptData2;
        }

        public void setEncryptData2(String encryptData2) {
            this.encryptData2 = encryptData2;
        }
    }

}
