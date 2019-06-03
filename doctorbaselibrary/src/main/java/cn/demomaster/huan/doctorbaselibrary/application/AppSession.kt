package yibai.com.yidao.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cn.demomaster.huan.doctorbaselibrary.application.MyApp
import cn.demomaster.huan.doctorbaselibrary.application.RSAServiceImpl
import cz.msebera.android.httpclient.Header
import org.jetbrains.anko.defaultSharedPreferences
import java.net.URLEncoder

class AppSession {

   /* val preference by lazy { MyApp.instance.defaultSharedPreferences }

    var clientPublicKey = ""
    var clientPrivateKey = ""

    var sessionId = preference.getString("sessionId", "")
    var token = preference.getString("token", "")
    var serverPublicKey  = preference.getString("serverPublicKey", "")
    var userName = preference.getString("userName", "")



    init {
        if (preference.contains("clientPublicKey") && preference.contains("clientPrivateKey")) {
            clientPublicKey = preference.getString("clientPublicKey", "")
            clientPrivateKey = preference.getString("clientPrivateKey", "")
        } else {
            val keyMap = RSAServiceImpl.initKey()!!
            clientPublicKey = RSAServiceImpl.getPublicKey(keyMap)
            clientPrivateKey = RSAServiceImpl.getPrivateKey(keyMap)

            preference.edit()
                    .putString("clientPublicKey", clientPublicKey)
                    .putString("clientPrivateKey", clientPrivateKey)
                    .apply()
        }
    }

   fun createSession() {
        val recreateSession = {
            HttpClient.defaultClient.get("/yidao/user/createSession".toHttpReq(), object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                    val msg = String(responseBody)
                    QLog.log("craete session", msg)
                    var json = JSONObject.parseObject(msg)
                    val data = json.getString("data")
                    json = JSON.parseObject(data)
                    sessionId = json.getString("sessionId")
                    serverPublicKey = json.getString("publicKey")

                    preference.edit()
                            .putString("sessionId", sessionId)
                            .putString("serverPublicKey", serverPublicKey)
                            .apply()

                    syncSessionClientKey()
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                }
            })
        }
        if (!token.isEmpty()) {
            // token是否过期
            val sign = RSAServiceImpl.encryptData(token, serverPublicKey)
            sign["token"] = sign["encryptData"]
            sign.remove("encryptData")
            sign["sessionId"] = sessionId
            var json = JSON.toJSONString(sign)
            json = URLEncoder.encode(json, "utf-8")
            HttpClient.defaultClient.post("/yidao/user/verifyToken".toHttpReq() + "?message=$json", object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                    val tokenInvalid = {
                        token = ""
                        serverPublicKey = ""
                        sessionId = ""
                        preference.edit()
                                .remove("token")
                                .remove("serverPublicKey")
                                .remove("sessionId")
                                .apply()
                        recreateSession()
                    }
                    try {
                        val msg = String(responseBody)
                        QLog.log("verify token", msg)
                        val json = JSONObject.parseObject(msg)
                        if (json.getIntValue("retCode") == 0) {
                            // 一切正常
                            syncSessionClientKey()
                        } else {
                            tokenInvalid()
                        }
                    } catch (e: Exception) {
                        tokenInvalid()
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                    recreateSession()
                }
            })
        } else {
            recreateSession()
        }
    }
companion object {

}
    fun syncSessionClientKey() {
        val params = mapOf(
                Pair("sessionId", sessionId),
                Pair("clientPublicKey", clientPublicKey)
        )
        val message = JSONObject.toJSONString(params)
        HttpClient.defaultClient.post("/yidao/user/syncSession".toHttpReq(), RequestParams(mapOf(
                Pair("message", message)
        )), object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val msg = String(responseBody)
                QLog.log(msg)
            }
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {}
        })
    }*/
}