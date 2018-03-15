package com.sfexpress.sfrouter.template

import com.sfexpress.sfrouter.annotation.models.RouteBaseData

/**
 * provider group模板，用来载入provider
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface IProviderGroup {

    fun loadInto(providers: MutableMap<String, RouteBaseData>)
}