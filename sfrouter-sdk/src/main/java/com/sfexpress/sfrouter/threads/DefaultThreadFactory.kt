package com.sfexpress.sfrouter.threads

import com.sfexpress.sfrouter.launcher.SFRouterManager
import com.sfexpress.sfrouter.utils.Consts

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 线程池工厂类，copy from Arouter
 * Created by sf-zhangpeng on 2018/3/14.
 */
class DefaultThreadFactory : ThreadFactory {

    private val threadNumber = AtomicInteger(1)
    private val group: ThreadGroup
    private val namePrefix: String

    init {
        val s = System.getSecurityManager()
        group = if (s != null) {
            s.threadGroup
        } else {
            Thread.currentThread().threadGroup
        }
        namePrefix = "SFRouterCore 任务池 No." + poolNumber.getAndIncrement() + ", 线程 No."
    }

    override fun newThread(runnable: Runnable): Thread {
        val threadName = namePrefix + threadNumber.getAndIncrement()
        SFRouterManager.logger.info(Consts.TAG, "线程已创建： [$threadName]")
        val thread = Thread(group, runnable, threadName, 0)
        if (thread.isDaemon) {   //设为非后台线程
            thread.isDaemon = false
        }
        if (thread.priority != Thread.NORM_PRIORITY) { //优先级为normal
            thread.priority = Thread.NORM_PRIORITY
        }
        // 捕获多线程处理中的异常
        thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, ex ->
            SFRouterManager.logger.info(Consts.TAG, "SFRouter后台线程任务出现异常! 线程 [" + thread.name + "], 错误信息 [" + ex.message + "]")
        }
        return thread
    }

    companion object {
        private val poolNumber = AtomicInteger(1)
    }
}