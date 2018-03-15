package com.sfexpress.sfrouter.launcher

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.widget.Toast
import com.sfexpress.sfrouter.annotation.enums.RouteTypeEnum
import com.sfexpress.sfrouter.callback.InterceptorCallback
import com.sfexpress.sfrouter.callback.NavigationCallback
import com.sfexpress.sfrouter.core.LogisticsCenter
import com.sfexpress.sfrouter.core.Postcard
import com.sfexpress.sfrouter.core.RouteCache
import com.sfexpress.sfrouter.exception.HandlerException
import com.sfexpress.sfrouter.exception.NoRouterFoundException
import com.sfexpress.sfrouter.service.AutoBindService
import com.sfexpress.sfrouter.service.DegradeService
import com.sfexpress.sfrouter.service.InterceptorService
import com.sfexpress.sfrouter.service.PathReplaceService
import com.sfexpress.sfrouter.template.ILogger
import com.sfexpress.sfrouter.utils.Consts
import com.sfexpress.sfrouter.utils.DefaultLogger
import java.util.*

@SuppressLint("StaticFieldLeak")
/**
 * SFRouter核心实现类，路由逻辑的处理都在这里，不建议业务层直接调用
 * Created by sf-zhangpeng on 2018/3/15.
 */
object SFRouterCore {

    var logger: ILogger = DefaultLogger() // 日志工具
    private var hasInit = false
    private var mContext: Application? = null
    private var interceptorService: InterceptorService? = null
    private var debuggable = false

    @Synchronized
    fun init(application: Application): Boolean {
        mContext = application
        LogisticsCenter.init(mContext!!)
        logger.info(Consts.TAG, "SFRouter初始化完成!")
        hasInit = true
        return true
    }

    /**
     * 销毁SFRouter，只能在debug模式使用
     */
    @Synchronized
    fun destroy() {
        if (debuggable()) {
            hasInit = false
            LogisticsCenter.suspend()
            logger.info(Consts.TAG, "SFRouter已销毁!")
        } else {
            logger.error(Consts.TAG, "销毁SFRouter，只能在debug模式使用!")
        }
    }

    @Synchronized
    fun openDebug() {
        debuggable = true
        logger.info(Consts.TAG, "SFRouterCore::开启debug模式")
    }

    fun debuggable(): Boolean {
        return debuggable
    }

    @Synchronized
    fun printStackTrace() {
        logger.showStackTrace(true)
        logger.info(Consts.TAG, "SFRouter printStackTrace")
    }

    fun inject(thiz: Any) {
        val autoBindService = SFRouterManager.build("/sfrouter/service/autobind").navigation() as AutoBindService
        autoBindService.autoBind(thiz)
    }

    /**
     * 通过路径url创建postcard
     */
    fun build(path: String): Postcard {
        var path = path
        if (TextUtils.isEmpty(path)) {
            throw HandlerException(Consts.TAG + "参数不合法!")
        } else {
            val pService = SFRouterManager.navigation(PathReplaceService::class.java)
            if (null != pService) {
                path = pService.forString(path)
            }
            return build(path, extractGroup(path))
        }
    }

    /**
     * 通过路径URI创建postcard
     */
    fun build(uri: Uri?): Postcard {
        var uri = uri
        if (null == uri || TextUtils.isEmpty(uri.toString())) {
            throw HandlerException(Consts.TAG + "参数不合法!")
        } else {
            val pService = SFRouterManager.navigation(PathReplaceService::class.java)
            if (null != pService) {
                uri = pService.forUri(uri)
            }
            return Postcard(uri.path, extractGroup(uri.path), uri, null)
        }
    }

    /**
     * 通过路径path和group来创建postcard
     */
    fun build(path: String, group: String?): Postcard {
        var path = path
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw HandlerException(Consts.TAG + "参数不合法!")
        } else {
            val pService = SFRouterManager.navigation(PathReplaceService::class.java)
            if (null != pService) {
                path = pService.forString(path)
            }
            return Postcard(path, group)
        }
    }

    /**
     * 找到路径对应的默认group信息
     */
    private fun extractGroup(path: String): String? {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw HandlerException(Consts.TAG + "Extract the default group failed, the path must be start with '/' and contain more than 2 '/'!")
        }
        return try {
            var routeBaseData = RouteCache.routes[path]
            var routeBaseData2 = RouteCache.groupsIndex[path]
            var routeBaseData3 = RouteCache.providers[Objects::class.java]
            var routeBaseData4 = RouteCache.providersIndex[path]
            var strGroup = routeBaseData?.group
            val defaultGroup = path.substring(1, path.indexOf("/", 1))
            if (TextUtils.isEmpty(defaultGroup)) {
                throw HandlerException(Consts.TAG + "Extract the default group failed! There's nothing between 2 '/'!")
            } else {
                defaultGroup
            }
        } catch (e: Exception) {
            logger.warning(Consts.TAG, "Failed to extract default group! " + e.message)
            null
        }

    }

    fun afterInit() {
        // 初始化拦截器
        interceptorService = SFRouterManager.build("/sfrouter/service/interceptor").navigation() as InterceptorService?
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> navigation(service: Class<out T>): T? {
        return try {
            var postcard = LogisticsCenter.buildProvider(service.name)
            if (null == postcard) {
                postcard = LogisticsCenter.buildProvider(service.simpleName)
            }
            LogisticsCenter.completion(postcard)
            postcard!!.getProvider() as? T
        } catch (ex: NoRouterFoundException) {
            logger.warning(Consts.TAG, ex.message!!)
            null
        }
    }

    fun navigation(context: Context?, postcard: Postcard, requestCode: Int, callback: NavigationCallback?): Any? {
        try {
            LogisticsCenter.completion(postcard)
        } catch (ex: NoRouterFoundException) {
            logger.warning(Consts.TAG, ex.message!!)
            if (debuggable()) { // Show friendly tips for user.
                Toast.makeText(mContext, "找不到对应的路由信息!\n" +
                        " Path = [" + postcard.path + "]\n" +
                        " Group = [" + postcard.group + "]", Toast.LENGTH_LONG).show()
            }
            if (null != callback) {
                callback.onLost(postcard)
            } else {    // No callback for this invoke, then we use the global degrade service.
                val degradeService = SFRouterManager.navigation(DegradeService::class.java)
                degradeService?.onLost(context!!, postcard)
            }
            return null
        }
        callback?.onFound(postcard)
        if (!postcard.isGreenChannel()) {   // It must be run in async thread, maybe interceptor cost too mush time made ANR.
            interceptorService?.doInterceptions(postcard, object : InterceptorCallback {
                override fun onContinue(postcard: Postcard) {
                    navigationCore(context, postcard, requestCode, callback)
                }

                override fun onInterrupt(exception: Throwable) {
                    callback?.onInterrupt(postcard)
                    logger.info(Consts.TAG, "跳转失败, 拦截器异常: " + exception.message)
                }
            })
        } else {
            return navigationCore(context, postcard, requestCode, callback)
        }
        return null
    }

    /**
     * 跳转逻辑的内核，本质上还是使用Android原生的跳转
     */
    private fun navigationCore(context: Context?, postcard: Postcard, requestCode: Int, callback: NavigationCallback?): Any? {
        val currentContext = context ?: mContext
        when (postcard.type) {
            RouteTypeEnum.ACTIVITY -> {
                val intent = Intent(currentContext, postcard.destination)
                intent.putExtras(postcard.getExtras())

                val flags = postcard.getFlag()
                if (-1 != flags) {
                    intent.flags = flags
                } else if (currentContext !is Activity) {    // Non activity, need less one flag.
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                Handler(Looper.getMainLooper()).post {
                    if (requestCode > 0) {  // Need start for result
                        ActivityCompat.startActivityForResult(currentContext as Activity, intent, requestCode, postcard.getOptionsBundle())
                    } else {
                        ActivityCompat.startActivity(currentContext, intent, postcard.getOptionsBundle())
                    }
                    if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) && currentContext is Activity) {    // Old version.
                        currentContext.overridePendingTransition(postcard.getEnterAnim(), postcard.getExitAnim())
                    }
                    callback?.onArrival(postcard)
                }
            }
            RouteTypeEnum.PROVIDER -> return postcard.getProvider()
            RouteTypeEnum.SERVICE -> return null
            else -> return null
        }
        return null
    }
}