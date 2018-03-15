package com.sfexpress.sfrouter.template

import com.sfexpress.sfrouter.annotation.models.RouteBaseData

/**
 * 路由group模板，用来载入路由信息
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface IRouteGroup {

    fun loadInto(routes: MutableMap<String, RouteBaseData>)
}
