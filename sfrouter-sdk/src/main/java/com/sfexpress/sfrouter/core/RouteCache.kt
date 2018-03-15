package com.sfexpress.sfrouter.core

import com.sfexpress.sfrouter.annotation.models.RouteBaseData
import com.sfexpress.sfrouter.template.IInterceptor
import com.sfexpress.sfrouter.template.IProvider
import com.sfexpress.sfrouter.template.IRouteGroup

/**
 * 路由信息缓存类
 * Created by sf-zhangpeng on 2018/3/13.
 */
object RouteCache {

    // 路由数据信息缓存
    var groupsIndex = mutableMapOf<String, Class<out IRouteGroup>>()
    var routes = mutableMapOf<String, RouteBaseData>()

    // provider缓存
    var providers = mutableMapOf<Class<*>, IProvider>()
    var providersIndex = mutableMapOf<String, RouteBaseData>()

    // 拦截器缓存
    var interceptorsIndex = UniqueKeyTreeMap<Int, Class<out IInterceptor>>("More than one interceptors use same priority [%s]")
    var interceptors = mutableListOf<IInterceptor>()

    fun clear() {
        routes.clear()
        groupsIndex.clear()
        providers.clear()
        providersIndex.clear()
        interceptors.clear()
        interceptorsIndex.clear()
    }
}