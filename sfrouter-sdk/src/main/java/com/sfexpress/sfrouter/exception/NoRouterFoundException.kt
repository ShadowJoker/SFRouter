package com.sfexpress.sfrouter.exception

/**
 * 找不到路由的异常
 * Created by sf-zhangpeng on 2018/3/13.
 */
class NoRouterFoundException(detailMessage: String) : RuntimeException(detailMessage)