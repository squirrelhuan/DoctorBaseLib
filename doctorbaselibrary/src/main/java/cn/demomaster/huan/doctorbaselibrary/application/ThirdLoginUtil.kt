package yibai.com.yidao.util
/*
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.sina.weibo.SinaWeibo
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat*/
import java.util.*

/**
 * Created by zhuozhongcao on 2017/10/31.
 */

object ThirdLoginUtil {

    /*val WEIXIN = 0
    val QICQ = 1
    val SINA = 2

    fun login(type: Int, callback: ThreeStringCallback) {
        val media =
                when (type) {
                    WEIXIN -> Wechat.NAME
                    QICQ -> QQ.NAME
                    SINA -> SinaWeibo.NAME
                    else -> SinaWeibo.NAME
                }
        val platform = ShareSDK.getPlatform(media)
        platform.SSOSetting(false)
        platform.platformActionListener = object : PlatformActionListener {
            override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
                println("授权成功")
                if (p1 == Platform.ACTION_AUTHORIZING) {
                    val unionId: String?
                    if (type == WEIXIN) {
                        unionId = platform.db.get("unionid")
                    } else {
                        unionId = platform.db.userId
                    }
                    val name: String? = platform.db.userName
                    val face: String? = platform.db.userIcon
                    callback(unionId!!, name!!, face!!)
                    println("$unionId $name $face")
                }
            }

            override fun onCancel(p0: Platform?, p1: Int) {
                println("取消授权")
            }

            override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
                println("授权失败")
            }
        }
        platform.authorize()
    }*/
}