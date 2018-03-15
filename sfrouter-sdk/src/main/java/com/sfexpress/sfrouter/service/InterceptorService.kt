package com.sfexpress.sfrouter.service

import com.sfexpress.sfrouter.callback.InterceptorCallback
import com.sfexpress.sfrouter.core.Postcard
import com.sfexpress.sfrouter.template.IProvider

/**
 * Interceptor服务
 * Created by sf-zhangpeng on 2018/3/14.
 */
interface InterceptorService : IProvider {

    fun doInterceptions(postcard: Postcard, callback: InterceptorCallback)
}
