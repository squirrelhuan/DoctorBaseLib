package yibai.com.yidao.fragment

/**
 * Created by zhuozhongcao on 2017/12/26.
 */

import android.Manifest
import android.app.Activity
import android.os.Looper
import androidx.fragment.app.Fragment
import android.webkit.*
import com.scwang.smartrefresh.layout.api.RefreshLayout
import org.jetbrains.anko.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import yibai.com.yidao.util.*
import java.net.URLEncoder


class SingleFragment : Fragment() {

    var webpage = ""

    lateinit var web: WebView
    var loadFinish = false
    var refresh: RefreshLayout? = null
    var backOverrided = false

    var afterPermissionCallback: UnitCallback? = null

    @AfterPermissionGranted(qrcodeCode)
    fun requestCameraPermission() {
        val perms = arrayOf(Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(activity!!, *perms)) {
            afterPermissionCallback?.invoke()
            afterPermissionCallback = null
        } else {
            EasyPermissions.requestPermissions(this, "需要使用相机扫描二维码", qrcodeCode, *perms)
        }
    }


    fun backPress() {
        if (backOverrided) {
            callbackWebviewResult("", "overrideBack")
        } else {
            activity!!.run {
                intent.putExtra("result", "")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    fun executeJS(js: String) {
        web.loadUrl("javascript:$js")
    }

    fun callbackWebviewResult(res: String, func: String) {
        var encode = URLEncoder.encode(res, "utf-8")
        encode = URLEncoder.encode(encode, "utf-8")
        executeJS("ybLifeCircle.onResult('$encode', '$func')")
    }

    fun callbackWebviewResult(res: Boolean, func: String) {
        executeJS("ybLifeCircle.onResult($res, '$func')")
    }

    fun callbackWebviewResult(res: Int, func: String) {
        executeJS("ybLifeCircle.onResult($res, '$func')")
    }


    fun errorOccur() {
        activity?.runOnUiThread {
            web.loadUrl("file:///android_asset/html/error.html")
        }
    }

    fun refreshWebView() {
        if (webpage == "") return
        loadFinish = false
        val task = {
            val req = webpage.toReq()
            web.loadUrl(req)
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            task()
        } else {
            activity!!.runOnUiThread {
                task()
            }
        }
    }

  /*  fun injectUid() {
        // 注入用户身份
        val res = AppSession.sessionId + "," + AppSession.token + "," + AppSession.userName
        val encode = URLEncoder.encode(res, "utf-8")

        executeJS("ybLifeCircle.injectUid('$encode')")
    }*/


    override fun onResume() {
        super.onResume()

        //injectUid()
        executeJS("ybLifeCircle.onShow()")
    }

    override fun onPause() {
        super.onPause()

        executeJS("ybLifeCircle.onHide()")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object {

        fun newInstance(url: String): SingleFragment {
            val fragment = SingleFragment()
            fragment.arguments = bundleOf(Pair("webpage", url))
            return fragment
        }

        const val jsResponseCode = 322
        const val qrcodeCode = 345
    }

}