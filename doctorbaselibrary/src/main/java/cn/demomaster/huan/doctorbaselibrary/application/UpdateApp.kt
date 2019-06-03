package yibai.com.yidao.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.alibaba.fastjson.JSON
import com.loopj.android.http.AsyncHttpResponseHandler
import cn.demomaster.huan.doctorbaselibrary.application.MyApp
import cn.demomaster.huan.doctorbaselibrary.model.AndroidUpdate
import cz.msebera.android.httpclient.Header
import org.jetbrains.anko.doAsync

/**
 * Created by zhuozhongcao on 2017/6/12.
 */

object UpdateApp {

    fun checkUpdate(activity: Activity) {
        val app = MyApp.getInstance()
        val versionCode = app.packageManager.getPackageInfo(app.packageName, 0).versionCode

        HttpClient.defaultClient.get("/api/androidversion?version=$versionCode".toReq(), object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                doAsync {
                    val update = JSON.parseObject(String(responseBody), AndroidUpdate::class.java)
                    if (update.update) {
                        activity.runOnUiThread {
                            download(activity, update.download, update.message)
                        }
                    }
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
            }

        })
    }

    private fun download(activity: Activity, url: String, desp: String) {
        AlertDialog.Builder(activity)
                .setTitle("新版本发布")
                .setMessage(desp)
                .setPositiveButton("去更新", { dialog, which ->
                    val i = Intent(Intent.ACTION_VIEW)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.data = Uri.parse(url)
                    MyApp.getInstance().startActivity(i)
                })
                .setNegativeButton("取消", null)
                .show()
    }
}