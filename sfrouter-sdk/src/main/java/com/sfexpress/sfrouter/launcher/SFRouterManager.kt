package com.sfexpress.sfrouter.launcher

import android.app.Application
import android.content.Context
import android.net.Uri
import com.sfexpress.sfrouter.callback.NavigationCallback
import com.sfexpress.sfrouter.core.Postcard
import com.sfexpress.sfrouter.utils.Consts

/**
 * SFRouter管理类，提供对外的统一接口，使用前必须进行初始化
 * Created by sf-zhangpeng on 2018/3/15.
 */
object SFRouterManager {

    const val RAW_URI = "NTeRQWvye18AkPd6G"
    const val AUTO_INJECT = "wmHzgD4lOj5o4241"
    private var hasInit = false
    var logger = SFRouterCore.logger

    /**
     * 初始化，调用其他接口以前必须进行初始化，否则会报错
     */
    fun init(application: Application) {
        if (!hasInit) {
            logger = SFRouterCore.logger
            SFRouterCore.logger.info(Consts.TAG, "SFRouter开始初始化.")
            hasInit = SFRouterCore.init(application)
            if (hasInit) {
                SFRouterCore.afterInit()
            }
            SFRouterCore.logger.info(Consts.TAG, "SFRouter初始化.")
        }
    }

    @Synchronized
    fun openDebug() {
        SFRouterCore.openDebug()
    }

    fun debuggable(): Boolean {
        return SFRouterCore.debuggable()
    }

    @Synchronized
    fun printStackTrace() {
        SFRouterCore.printStackTrace()
    }

    @Synchronized
    fun destroy() {
        SFRouterCore.destroy()
        hasInit = false
    }

    /**
     * 注入参数
     */
    fun inject(thiz: Any) {
        SFRouterCore.inject(thiz)
    }

    /**
     * 通过路径URL创建postcard
     */
    fun build(path: String): Postcard {
        return SFRouterCore.build(path)
    }

    /**
     * 通过路径URL和group来创建postcard
     */
    fun build(path: String, group: String): Postcard {
        return SFRouterCore.build(path, group)
    }

    /**
     * 通过URI创建postcard
     */
    fun build(url: Uri): Postcard {
        return SFRouterCore.build(url)
    }

    /**
     * 根据类跳转
     **/
    fun <T> navigation(service: Class<out T>): T? {
        return SFRouterCore.navigation(service)
    }

    /**
     * 启动跳转
     */
    fun navigation(mContext: Context?, postcard: Postcard, requestCode: Int, callback: NavigationCallback?): Any? {
        return SFRouterCore.navigation(mContext, postcard, requestCode, callback)
    }
}