package com.sfexpress.sfrouter.callback

import com.sfexpress.sfrouter.core.Postcard

/**
 * 拦截器回调
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface InterceptorCallback {

    /**
     * 发生拦截时的回调
     */
    fun onInterrupt(exception: Throwable)

    /**
     * 放行继续执行的回调
     */
    fun onContinue(postcard: Postcard) {}
}
