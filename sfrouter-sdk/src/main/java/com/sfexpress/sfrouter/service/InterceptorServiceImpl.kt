package com.sfexpress.sfrouter.service

import android.content.Context
import com.sfexpress.sfrouter.annotation.Route
import com.sfexpress.sfrouter.callback.InterceptorCallback
import com.sfexpress.sfrouter.core.LogisticsCenter
import com.sfexpress.sfrouter.core.Postcard
import com.sfexpress.sfrouter.core.RouteCache
import com.sfexpress.sfrouter.exception.HandlerException
import com.sfexpress.sfrouter.launcher.SFRouterManager
import com.sfexpress.sfrouter.threads.CancelableCountDownLatch
import com.sfexpress.sfrouter.threads.DefaultPoolExecutor
import com.sfexpress.sfrouter.utils.Consts.TAG
import org.apache.commons.collections4.MapUtils
import java.util.concurrent.TimeUnit

/**
 * 拦截器服务的实现
 * Created by sf-zhangpeng on 2018/3/14.
 */
@Route(path = "/sfrouter/service/interceptor")
class InterceptorServiceImpl : InterceptorService {

    override fun doInterceptions(postcard: Postcard, callback: InterceptorCallback) {
        if (RouteCache.interceptors.size > 0) {
            checkInterceptorsInitStatus()
            if (!interceptorHasInit) {
                callback.onInterrupt(HandlerException("拦截器初始化超时!"))
                return
            }
            DefaultPoolExecutor.execute {
                val interceptorCounter = CancelableCountDownLatch(RouteCache.interceptors.size)
                try {
                    excute(0, interceptorCounter, postcard)
                    interceptorCounter.await(postcard.getTimeout().toLong(), TimeUnit.SECONDS)
                    when {
                        interceptorCounter.count > 0 -> {
                            // 如果拦截器技术没有归零，说明有异常
                            callback.onInterrupt(HandlerException("拦截器处理超时"))
                        }
                        postcard.getTag() != null -> {
                            // 检查异常tag中是否有异常
                            callback.onInterrupt(HandlerException(postcard.getTag()!!.toString()))
                        }
                        else -> callback.onContinue(postcard)
                    }
                } catch (e: Exception) {
                    callback.onInterrupt(e)
                }
            }
        } else {
            callback.onContinue(postcard)
        }
    }

    override fun init(context: Context) {
        DefaultPoolExecutor.execute {
            if (MapUtils.isNotEmpty(RouteCache.interceptorsIndex)) {
                for ((_, interceptorClass) in RouteCache.interceptorsIndex) {
                    try {
                        val iInterceptor = interceptorClass.getConstructor().newInstance()
                        iInterceptor.init(context)
                        RouteCache.interceptors.add(iInterceptor)
                    } catch (ex: Exception) {
                        throw HandlerException(TAG + "SFRouter拦截器初始化错误! [" + interceptorClass.name + "], 原因 [" + ex.message + "]")
                    }

                }

                interceptorHasInit = true

                SFRouterManager.logger.info(TAG, "SFRouter拦截器初始化完成.")

                synchronized(interceptorInitLock) {
                    interceptorInitLock.notifyAll()
                }
            }
        }
    }

    companion object {
        private var interceptorHasInit: Boolean = false
        private val interceptorInitLock = Object()

        /**
         * 执行拦截处理逻辑
         */
        private fun excute(index: Int, counter: CancelableCountDownLatch, postcard: Postcard) {
            if (index < RouteCache.interceptors.size) {
                val iInterceptor = RouteCache.interceptors[index]
                iInterceptor.process(postcard, object : InterceptorCallback {
                    override fun onContinue(postcard: Postcard) {
                        // 上一个拦截器执行完毕会继续走这里，循环直至计数归零
                        counter.countDown()
                        excute(index + 1, counter, postcard)
                    }

                    override fun onInterrupt(exception: Throwable) {
                        // 上一个拦截器执行发生异常，会走这里
                        postcard.setTag(exception.message!!) // 将异常信息保存到tag中
                        counter.cancel()
                    }
                })
            }
        }

        private fun checkInterceptorsInitStatus() {
            synchronized(interceptorInitLock) {
                while (!interceptorHasInit) {
                    try {
                        interceptorInitLock.wait((10 * 1000).toLong())
                    } catch (e: InterruptedException) {
                        throw HandlerException(TAG + "拦截器初始化超时! [" + e.message + "]")
                    }

                }
            }
        }
    }
}
