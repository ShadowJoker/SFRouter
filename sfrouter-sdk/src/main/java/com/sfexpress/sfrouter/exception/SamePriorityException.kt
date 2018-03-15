package com.sfexpress.sfrouter.exception

/**
 * 拦截器优先级相同发生冲突的异常
 * Created by sf-zhangpeng on 2018/3/13.
 */
class SamePriorityException(detailMessage: String) : RuntimeException(detailMessage)