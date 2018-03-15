package com.sfexpress.sfrouter.threads

import com.sfexpress.sfrouter.launcher.SFRouterManager
import com.sfexpress.sfrouter.utils.Consts
import com.sfexpress.sfrouter.utils.TextUtils
import java.util.concurrent.*

/**
 * 异步线程池执行器，copy from Arouter
 * Created by sf-zhangpeng on 2018/3/14.
 */
object DefaultPoolExecutor : ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1,
        Runtime.getRuntime().availableProcessors() + 1,
        30L,
        TimeUnit.SECONDS,
        ArrayBlockingQueue(64),
        DefaultThreadFactory()) {

    /**
     *  线程执行结束，检查异常
     */
    override fun afterExecute(r: Runnable, t: Throwable?) {
        var throwable = t
        super.afterExecute(r, throwable)
        if (throwable == null && r is Future<*>) {
            try {
                (r as Future<*>).get()
            } catch (ce: CancellationException) {
                throwable = ce
            } catch (ee: ExecutionException) {
                throwable = ee.cause
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
        if (throwable != null) {
            SFRouterManager.logger.warning(Consts.TAG, "SFRouter后台线程任务出现异常! 线程 [" + Thread.currentThread().name + "], 错误信息 [" + throwable.message + "]\n" + TextUtils.formatStackTrace(throwable.stackTrace))
        }
    }
}
