package yibai.com.yidao.util

import android.app.Activity

//import yibai.com.yidao.activity.SingleFragmentActivity

//import yibai.com.yidao.fragment.SingleFragment

/**
 * Created by zhuozhongcao on 2017/12/26.
 */

//val base = "http://192.168.31.51:4576"
val base = "http://www.fegofficial.com:4576"
//
fun String.toReq(): String = if (this.startsWith("http") || this.startsWith("file:///")) this else "${base}$this"

fun String.toHttpReq() = "http://47.96.162.168:8080${this}"
/*
fun Activity.startPage(webpage: String, immerse: Boolean) {
    val i = Intent(this, SingleFragmentActivity::class.java)
    i.putExtra("webpage", webpage)
    i.putExtra("immerse", immerse)
    this.startActivityForResult(i, SingleFragment.jsResponseCode)
}*/

fun Activity.dip2px(dipValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun Activity.px2dip(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}