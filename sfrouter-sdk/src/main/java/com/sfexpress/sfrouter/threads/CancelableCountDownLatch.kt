package com.sfexpress.sfrouter.threads

import java.util.concurrent.CountDownLatch

/**
 * 线程计数器，基于Java并发库的CountDownLatch实现。主要用在多个拦截器的拦截逻辑执行上。
 * 自定义了取消功能，copy from Arouter
 * Created by sf-zhangpeng on 2018/3/14.
 */
class CancelableCountDownLatch(count: Int) : CountDownLatch(count) {

    fun cancel() {
        while (count > 0) {
            countDown()
        }
    }
}
