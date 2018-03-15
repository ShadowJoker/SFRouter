package com.sfexpress.sfrouter.template

/**
 * 注射器模板，用来自动绑定属性
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface IInjector {

    /**
     * 执行注入逻辑
     */
    fun inject(target: Any)
}
