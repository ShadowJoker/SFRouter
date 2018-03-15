package com.sfexpress.sfrouter.template

import com.sfexpress.sfrouter.callback.InterceptorCallback
import com.sfexpress.sfrouter.core.Postcard

/**
 * 拦截器模板，在跳转时业务层可以插入拦截逻辑
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface IInterceptor : IProvider {

    /**
     * 拦截器的处理
     */
    fun process(postcard: Postcard, callback: InterceptorCallback)
}
