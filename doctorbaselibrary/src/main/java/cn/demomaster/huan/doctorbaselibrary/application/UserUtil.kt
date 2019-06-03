package yibai.com.yidao.util

import cn.demomaster.huan.doctorbaselibrary.application.MyApp
import org.jetbrains.anko.defaultSharedPreferences


/**
 * Created by zhuozhongcao on 2017/12/26.
 */

object UserUtil {

    private val preference by lazy { MyApp.getInstance().defaultSharedPreferences }

  /*  fun decryptLoginData(res: String) {
        val model = JSON.parseObject(res, UserModelApi::class.java)
        val token = RSAServiceImpl.decryptData(model.token, model.uuid, AppSession.clientPrivateKey)
        preference.edit()
                .putString("token", token)
                .putString("userName", model.userName)
                .apply()
        AppSession.token = token
        AppSession.userName = model.userName
    }*/

}


