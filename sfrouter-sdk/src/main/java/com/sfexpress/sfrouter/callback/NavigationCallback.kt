package com.sfexpress.sfrouter.callback

import com.sfexpress.sfrouter.core.Postcard

/**
 * 路由跳转过程的回调
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface NavigationCallback {

    /**
     * 找到目标地址时的回调
     */
    fun onFound(postcard: Postcard) {}

    /**
     * 导航失败时的回调
     */
    fun onLost(postcard: Postcard) {}

    /**
     * 跳转结束时的回调
     */
    fun onArrival(postcard: Postcard)

    /**
     * 跳转过程被中断的回到
     */
    fun onInterrupt(postcard: Postcard) {}
}
