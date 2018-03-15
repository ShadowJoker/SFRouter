package com.sfexpress.sfrouter.template

/**
 * 路由的Root元素模板
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface IRouteRoot {

    fun loadInto(routes: MutableMap<String, Class<out IRouteGroup>>)
}
