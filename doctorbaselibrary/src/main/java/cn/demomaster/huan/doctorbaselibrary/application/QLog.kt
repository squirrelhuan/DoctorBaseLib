package yibai.com.yidao.util

import android.util.Log

/**
 * Created by zhuozhongcao on 2018/6/25.
 */

object QLog {

    fun log(vararg args: Any) {
        args.forEach {
            Log.i("tags", it.toString())
        }
    }
}