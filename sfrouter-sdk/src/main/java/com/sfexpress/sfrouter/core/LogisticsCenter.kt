package com.sfexpress.sfrouter.core

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import com.sfexpress.sfrouter.annotation.enums.FieldTypeEnum
import com.sfexpress.sfrouter.annotation.enums.RouteTypeEnum
import com.sfexpress.sfrouter.compiler.util.StaticConsts.PACKAGE_OF_GENERATE_FILE
import com.sfexpress.sfrouter.exception.HandlerException
import com.sfexpress.sfrouter.exception.NoRouterFoundException
import com.sfexpress.sfrouter.launcher.SFRouterManager
import com.sfexpress.sfrouter.launcher.SFRouterManager.logger
import com.sfexpress.sfrouter.template.IInterceptorGroup
import com.sfexpress.sfrouter.template.IProvider
import com.sfexpress.sfrouter.template.IProviderGroup
import com.sfexpress.sfrouter.template.IRouteRoot
import com.sfexpress.sfrouter.utils.ClassUtils
import com.sfexpress.sfrouter.utils.Consts
import com.sfexpress.sfrouter.utils.Consts.DOT
import com.sfexpress.sfrouter.utils.Consts.SDK_NAME
import com.sfexpress.sfrouter.utils.Consts.SEPARATOR
import com.sfexpress.sfrouter.utils.Consts.SFROUTER_SP_CACHE_KEY
import com.sfexpress.sfrouter.utils.Consts.SFROUTER_SP_KEY_MAP
import com.sfexpress.sfrouter.utils.Consts.SUFFIX_INTERCEPTORS
import com.sfexpress.sfrouter.utils.Consts.SUFFIX_PROVIDERS
import com.sfexpress.sfrouter.utils.Consts.SUFFIX_ROOT
import com.sfexpress.sfrouter.utils.Consts.TAG
import com.sfexpress.sfrouter.utils.MapUtils
import com.sfexpress.sfrouter.utils.PackageUtils
import java.util.*

@SuppressLint("StaticFieldLeak")
/**
 * 路由中心
 * 注意：初始化的时候一定要传入ApplicationContext，否则会出现内存泄露
 * Created by sf-zhangpeng on 2018/3/13.
 */
object LogisticsCenter {
    private var mContext: Context? = null

    /**
     * 路由中心初始化，将所有元数据载入内存
     */
    @Synchronized
    @Throws(HandlerException::class)
    fun init(context: Context) {
        mContext = context

        try {
            var startInit = System.currentTimeMillis()
            val routerMap: Set<String>

            // 【第一步】查询路由信息
            // debug包或者初次安装，都要重新创建路由信息。
            if (SFRouterManager.debuggable() || PackageUtils.isNewVersion(context)) {
                logger.info(TAG, "Debug包或初次安装，重建路由表")
                // 载入编译器自动生成的类
                routerMap = ClassUtils.getFileNameByPackageName(mContext!!, PACKAGE_OF_GENERATE_FILE)
                if (!routerMap.isEmpty()) {
                    context.getSharedPreferences(SFROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).edit().putStringSet(SFROUTER_SP_KEY_MAP, routerMap).apply()
                }
                // 更新路由信息后，将对应的版本信息保存
                PackageUtils.updateVersion(context)
            } else {
                logger.info(TAG, "从SP缓存中加载路由信息。")
                routerMap = HashSet(context.getSharedPreferences(SFROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).getStringSet(SFROUTER_SP_KEY_MAP, HashSet())!!)
            }
            logger.info(TAG, "查询路由信息结束, 路由信息数目 = " + routerMap.size + ", 耗时 " + (System.currentTimeMillis() - startInit) + " 毫秒.")
            startInit = System.currentTimeMillis()

            // 【第二步】加载路由信息
            for (className in routerMap) {
                when {
                // 加载Root节点
                    className.startsWith(PACKAGE_OF_GENERATE_FILE + DOT + SDK_NAME + SEPARATOR + SUFFIX_ROOT) -> {
                        (Class.forName(className).getConstructor().newInstance() as IRouteRoot).loadInto(RouteCache.groupsIndex)
                    }
                // 加载拦截器信息
                    className.startsWith(PACKAGE_OF_GENERATE_FILE + DOT + SDK_NAME + SEPARATOR + SUFFIX_INTERCEPTORS) -> {
                        (Class.forName(className).getConstructor().newInstance() as IInterceptorGroup).loadInto(RouteCache.interceptorsIndex)
                    }
                // 加载provider信息
                    className.startsWith(PACKAGE_OF_GENERATE_FILE + DOT + SDK_NAME + SEPARATOR + SUFFIX_PROVIDERS) -> {
                        (Class.forName(className).getConstructor().newInstance() as IProviderGroup).loadInto(RouteCache.providersIndex)
                    }
                }
            }
            logger.info(TAG, "加载路由信息结束, 耗时 " + (System.currentTimeMillis() - startInit) + " 毫秒.")

            if (RouteCache.groupsIndex.isEmpty()) {
                logger.error(TAG, "找不到路由信息文件, 检查代码设置!")
            }

            if (SFRouterManager.debuggable()) {
                logger.debug(TAG, String.format(Locale.getDefault(), "路由中心初始化完毕, GroupIndex[%d], InterceptorIndex[%d], ProviderIndex[%d]", RouteCache.groupsIndex.size, RouteCache.interceptorsIndex.size, RouteCache.providersIndex.size))
            }
        } catch (e: Exception) {
            throw HandlerException(TAG + "SFRouter路由中心初始化异常! [" + e.message + "]")
        }
    }

    /**
     * 根据服务名创建对应的provider
     */
    fun buildProvider(serviceName: String): Postcard? {
        val meta = RouteCache.providersIndex[serviceName]
        return if (null == meta) {
            null
        } else {
            Postcard(meta.path, meta.group)
        }
    }

    fun setValue(postcard: Postcard, typeDef: Int, key: String, value: String?) {
        if (value == null || TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return
        }
        try {
            when (typeDef) {
                FieldTypeEnum.BOOLEAN.ordinal -> postcard.withBoolean(key, value.toBoolean())
                FieldTypeEnum.BYTE.ordinal -> postcard.withByte(key, value.toByte())
                FieldTypeEnum.SHORT.ordinal -> postcard.withShort(key, value.toShort())
                FieldTypeEnum.INT.ordinal -> postcard.withInt(key, Integer.valueOf(value))
                FieldTypeEnum.LONG.ordinal -> postcard.withLong(key, value.toLong())
                FieldTypeEnum.FLOAT.ordinal -> postcard.withFloat(key, value.toFloat())
                FieldTypeEnum.DOUBLE.ordinal -> postcard.withDouble(key, value.toDouble())
                FieldTypeEnum.STRING.ordinal -> postcard.withString(key, value)
                FieldTypeEnum.PARCELABLE.ordinal -> {
                    // TODO : How to description parcelable value with string?
                }
                FieldTypeEnum.OBJECT.ordinal -> postcard.withString(key, value)
                else -> postcard.withString(key, value)
            }
        } catch (ex: Throwable) {
            logger.warning(Consts.TAG, "路由中心setValue失败!" + ex.message)
        }
    }

    /**
     * 完成postcard的封装
     *
     * @param postcard Incomplete postcard, should completion by this method.
     */
    @Synchronized
    fun completion(postcard: Postcard?) {
        if (null == postcard) {
            throw NoRouterFoundException(TAG + "postcard不能为null!")
        }

        val routeMeta = RouteCache.routes[postcard.path]
        if (null == routeMeta) {
            val groupMeta = RouteCache.groupsIndex[postcard.group]
            if (null == groupMeta) {
                throw NoRouterFoundException(TAG + "找不到对应路由 [" + postcard.path + "], group信息： [" + postcard.group + "]")
            } else {
                // Load route and cache it into memory, then delete from metas.
                try {
                    if (SFRouterManager.debuggable()) {
                        logger.debug(TAG, String.format(Locale.getDefault(), "加载group [%s], 由 [%s] 触发", postcard.group, postcard.path))
                    }
                    val iGroupInstance = groupMeta.getConstructor().newInstance()
                    iGroupInstance.loadInto(RouteCache.routes)
                    RouteCache.groupsIndex.remove(postcard.group)

                    if (SFRouterManager.debuggable()) {
                        logger.debug(TAG, String.format(Locale.getDefault(), "group [%s] 加载完成, 由 [%s] 触发", postcard.group, postcard.path))
                    }
                } catch (e: Exception) {
                    throw HandlerException(TAG + "加载group异常： [" + e.message + "]")
                }
                completion(postcard)   // Reload
            }
        } else {
            postcard.destination = routeMeta.destination
            postcard.type = routeMeta.type
            postcard.priority = routeMeta.priority
            postcard.extra = routeMeta.extra

            val rawUri = postcard.getUri()
            if (null != rawUri) {   // 向bundle中设置参数
                val resultMap = com.sfexpress.sfrouter.utils.TextUtils.splitQueryParameters(rawUri)
                val paramsType = routeMeta.paramsType

                if (MapUtils.isNotEmpty(paramsType)) {
                    for (params in paramsType.entries) {
                        setValue(postcard,
                                params.value,
                                params.key,
                                resultMap[params.key])
                    }
                    // 保存需要自动绑定的参数
                    postcard.getExtras()?.putStringArray(SFRouterManager.AUTO_INJECT, paramsType.keys.toTypedArray())
                }

                // 保存rawURL
                postcard.withString(SFRouterManager.RAW_URI, rawUri.toString())
            }

            @Suppress("UNCHECKED_CAST")
            when (routeMeta.type) {
                RouteTypeEnum.PROVIDER -> {
                    // 如果是provider类型，需要找到赌赢的实例，设置到postcard中
                    val providerMeta = routeMeta.destination as Class<out IProvider>
                    var instance = RouteCache.providers[providerMeta]
                    if (null == instance) {
                        val provider: IProvider
                        try {
                            provider = providerMeta.getConstructor().newInstance()
                            provider.init(mContext!!)
                            RouteCache.providers[providerMeta] = provider
                            instance = provider
                        } catch (e: Exception) {
                            throw HandlerException("初始化provider失败! " + e.message)
                        }
                    }
                    postcard.setProvider(instance!!)
                    postcard.greenChannel()    // Provider是服务，都应该跳过拦截器
                }
                else -> {
                }
            }
        }
    }

    /**
     * 停止操作，清理缓存
     */
    fun suspend() {
        RouteCache.clear()
    }
}