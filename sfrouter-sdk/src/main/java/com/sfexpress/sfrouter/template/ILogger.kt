package com.sfexpress.sfrouter.template

import com.sfexpress.sfrouter.utils.Consts

/**
 * 日志类模板
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface ILogger {

    fun showLog(isShowLog: Boolean)

    fun showStackTrace(isShowStackTrace: Boolean)

    fun debug(tag: String, message: String)

    fun info(tag: String, message: String)

    fun warning(tag: String, message: String)

    fun error(tag: String, message: String)

    fun getDefaultTag(): String

    companion object {
        val isShowLog = false
        val isShowStackTrace = false
        val defaultTag = Consts.TAG
    }
}
