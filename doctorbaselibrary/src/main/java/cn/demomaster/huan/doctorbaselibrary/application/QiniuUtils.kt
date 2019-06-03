package yibai.com.yidao.util

import com.alibaba.fastjson.JSON
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.AsyncHttpClient
import com.qiniu.android.common.FixedZone
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UpCompletionHandler
import com.qiniu.android.storage.UploadManager
import cz.msebera.android.httpclient.Header


/**
 * Created by zhuozhongcao on 2017/9/26.
 */


object QiniuUtils {

    private var token = ""
    private var cdn = ""
    private val config = Configuration.Builder()
            .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
            .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
            .connectTimeout(10)           // 链接超时。默认10秒
            .responseTimeout(60)          // 服务器响应超时。默认60秒
            .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
            .build()
    private val uploadManager = UploadManager(config)

    fun init() {
        HttpClient.defaultClient.get("http://www.fegofficial.com/qiniu/token".toReq(), object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    val json = JSON.parseObject(String(responseBody))
                    val data = JSON.parseObject(json.getString("data"))
                    token = data.getString("token")
                    cdn = data.getString("cdn")
                } catch (e: Exception) {
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseBody: ByteArray?, error: Throwable) {
                error.printStackTrace(System.out)
            }
        })
    }

    fun urlByKey(key: String): String {
        return "$cdn/$key"
    }


    fun uploadImage(filePath: String, handler: UpCompletionHandler) {
        uploadManager.put(filePath, null, token, handler, null)
    }

}