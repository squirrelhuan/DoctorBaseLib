package cn.demomaster.huan.doctorbaselibrary.application;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.apache.commons.codec.binary.Base64;
//import yibai.com.yidao.util.QLog;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RSAServiceImpl {
    public static final String KEY_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    /** 指定uuid加密算法为RSA */
    private static final String ALGORITHM = "AES";

    public static byte[] decryptBASE64(String key) {
        return android.util.Base64.decode(key, android.util.Base64.DEFAULT);
    }

    public static String encryptBASE64(byte[] bytes) {
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
    }


    public static byte[] decryptByPrivateKey(byte[] data, String key){
        try {
            // 对密钥解密
            byte[] keyBytes = decryptBASE64(key);
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密

     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(String data, String key) {
        return decryptByPrivateKey(decryptBASE64(data),key);
    }

    /**
     * 解密

     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key){
        try {
            // 对密钥解密
            byte[] keyBytes = decryptBASE64(key);
            // 取得公钥
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加密

     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(String data, String key){
        try {
            // 对公钥解密
            byte[] keyBytes = decryptBASE64(key);
            // 取得公钥
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data.getBytes());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 加密

     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key) {
        try {
            // 对密钥解密
            byte[] keyBytes = decryptBASE64(key);
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Key> keyMap){
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encryptBASE64(key.getEncoded());
    }

    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Key> keyMap){
        Key key = keyMap.get(PUBLIC_KEY);
        return encryptBASE64(key.getEncoded());
    }

    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Key> initKey() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator
                    .getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            Map<String, Key> keyMap = new HashMap(2);
            keyMap.put(PUBLIC_KEY, keyPair.getPublic());// 公钥
            keyMap.put(PRIVATE_KEY, keyPair.getPrivate());// 私钥
            return keyMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static String generateKeyString()
    {
        //必须长度为16
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
    }

    private static Key generateKey(String keyString) throws Exception {
        Key key = new SecretKeySpec(keyString.getBytes(), ALGORITHM);
        return key;
    }

    private static org.bouncycastle.jce.provider.BouncyCastleProvider bouncyCastleProvider;
    /**
     * 用uuid来进行加密的操作
     */
    public static String encrypt(String keyString, String data)
            throws Exception {
        Key key = generateKey(keyString);
        if(bouncyCastleProvider==null){
            bouncyCastleProvider =new org.bouncycastle.jce.provider.BouncyCastleProvider();
        }
        Security.addProvider(bouncyCastleProvider);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = encryptBASE64(encVal);
        return encryptedValue;
    }

    /**
     * 用uuid来进行解密的操作
     */
    public static String decrypt(String keyString, String encryptedData) throws Exception {
        Key key = generateKey(keyString);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = decryptBASE64(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    /**
     * 加密函数
     *
     * @param data：加密的数据
     * @param serverPublicKey：服务器端公钥
     * @return  {"uuid":***,"encryptData":****}
     * @throws Exception
     */
    public static Map encryptData(String data,String serverPublicKey) throws Exception{
        //生成客户端uuid
        String keyString=generateKeyString();
        //QLog.INSTANCE.log("--keyString-", JSON.toJSONString(mapOf("keyString", keyString)), "---");
        //uuid加密有效数据
        String encryptData= encrypt(keyString, data);
        //QLog.INSTANCE.log("--encrypt-", JSON.toJSONString(mapOf("encryptData", encryptData)), "---");
        //用服务器端公钥加密uuid
        String uuid=encryptBASE64(encryptByPublicKey(keyString, serverPublicKey));
        //QLog.INSTANCE.log("--uuid-", JSON.toJSONString(mapOf("uuid", uuid)), "---");
        Map<String,String> map=new HashMap<>();
        map.put("uuid",uuid);
        map.put("encryptData",encryptData);
        return map;
    }

    public static Map<String, String> mapOf(String key, String val) {
        Map<String, String> map = new HashMap<>();
        map.put(key, val);
        return map;
    }

    /**
     * 加密函数
     *
     * @param serverPublicKey：服务器端公钥
     * @return  {"uuid":***,"encryptData":****}
     * @throws Exception
     */
    public static Map encryptDubleData(String data1, String data2, String serverPublicKey) throws Exception{
        //生成客户端uuid
        String keyString=generateKeyString();
        //uuid加密有效数据
        String encryptData1 = encrypt(keyString, data1);
        String encryptData2 = encrypt(keyString, data2);
        //用服务器端公钥加密uuid
        String uuid=encryptBASE64(encryptByPublicKey(keyString, serverPublicKey));
        Map<String,String> map=new HashMap<>();
        map.put("uuid",uuid);
        map.put("encryptData1",encryptData1);
        map.put("encryptData2",encryptData2);
        return map;
    }

    public static String decryptData(String encryptData,String uuid,String clientPrivateKey) throws Exception{
        //用客户端私钥对uuid解密
        byte[] decryptUUID = decryptByPrivateKey(uuid, clientPrivateKey);
        String keyString=new String(decryptUUID);
        //用解密后的uuid解密加密数据
        String data=decrypt(keyString,encryptData);
        return data;
    }

    public static  void  main(String[] args) throws Exception
    {

        //生成公钥和私钥
        String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDg88D9TSf8rGCAmJ3mGP1+TOXU\n" +
                "oiOYS9OVf/XKrQ6dQj8NpelATYun01+Prsb3FFeLRxVVC9S0SKUtAVRzKPDwz7xp\n" +
                "dTtjGE/yq8F0EN6Q7pP4F4z8EOAytztvU6+ncehieJwvFqVrtTLKRhLfZ3Pt9Mzp\n" +
                "SxaSdqyFrE1ZQVd0RwIDAQAB";
        String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAODzwP1NJ/ysYICY\n" +
                "neYY/X5M5dSiI5hL05V/9cqtDp1CPw2l6UBNi6fTX4+uxvcUV4tHFVUL1LRIpS0B\n" +
                "VHMo8PDPvGl1O2MYT/KrwXQQ3pDuk/gXjPwQ4DK3O29Tr6dx6GJ4nC8WpWu1MspG\n" +
                "Et9nc+30zOlLFpJ2rIWsTVlBV3RHAgMBAAECgYBxPWOWX6PUh3Xg5nL+JPBfBxjf\n" +
                "bGucu/ccGBlEWM+1jGavWSjNLPwzlK1TaDlSohPb0gHEGTuPMc6slDHN8vsuXJJi\n" +
                "0MvOuwJ1EFV2Dlx7S3VzBpiFX5hRDGNx+KI9EUy7GtHHJ15JExVNaRhoD7DBSCE8\n" +
                "FjSoSOstOuYzi2jJeQJBAPu9gZea9TbKLSuCVcZlmpaKjwSDU0RilqQ2P0wrB/Sf\n" +
                "86/YIOP/r1lXcg5c/3I/T1Lt4GRuMPzindyW7Hn8WEsCQQDkwjTXKHPZfWP0m1jU\n" +
                "1tOA2s9K3U4iNuIBxj73VmSgwQnbf6drafWdXnTPGnxchvsy1XMqpHNXgrOQlYst\n" +
                "LQ51AkBUxHdCyZcQn/udzwF7EPOBBZ8q8d20BU6cdPfTehnvsXypFandnFc7SkrJ\n" +
                "s18A5XicgCUzLDeqr9RgVyHB4csJAkEA3hE5QnTeoY777YSwo004XKoM+VwEcWpd\n" +
                "0+MjVO/lF7jNXOt7FubxrCb5teK8PwY57IsRFaA26AjVT9IcHWrT0QJBAMK7lWHR\n" +
                "NbqPoBLGzdCV3togk4yAgcxN/a4xmepTdEoFiuN8ADPvPXjfKGeXBAR/CtGd4zgs\n" +
                "cwQkOiYKZVJ6EOA=";
//        String uuid="39e35c6e2cf244da";
//        Map<String,String> map=RSAServiceImpl.encryptData("39e35c6e2cf244da",publicKey);
//        System.out.println(map.get("uuid"));
//        System.out.println("*******************");
//        System.out.println(map.get("encryptData"));
////        Log.i("tags", map.get("uuid") + " ----  " + map.get("encryptData"));
//        uuid=map.get("uuid");
//        String encryptData=map.get("encryptData");
//        String data=RSAServiceImpl.decryptData(encryptData,uuid,privateKey);
//        System.out.println("*******************");
//        System.out.println(data);

        String rawdata="888666emma";
        String encryptdata= encrypt("39e35c6e2cf244da",  rawdata);
        System.out.println("*******************");
        System.out.println(encryptdata);

        String decryptData=decrypt("39e35c6e2cf244da", encryptdata);
        System.out.println("*******************");
        System.out.println(decryptData);

    }
}

