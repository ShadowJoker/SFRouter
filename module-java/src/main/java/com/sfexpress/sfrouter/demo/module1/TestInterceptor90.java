package com.sfexpress.sfrouter.demo.module1;

import android.content.Context;
import android.util.Log;

import com.sfexpress.sfrouter.annotation.RouteInterceptor;
import com.sfexpress.sfrouter.callback.InterceptorCallback;
import com.sfexpress.sfrouter.core.Postcard;
import com.sfexpress.sfrouter.template.IInterceptor;

/**
 * TODO feature
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/9/9 14:34
 */
@RouteInterceptor(priority = 90)
public class TestInterceptor90 implements IInterceptor {
    /**
     * The operation of this tollgate.
     *
     * @param postcard meta
     * @param callback cb
     */
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        callback.onContinue(postcard);
    }

    /**
     * Do your init work in this method, it well be call when processor has been load.
     *
     * @param context ctx
     */
    @Override
    public void init(Context context) {
        Log.e("test", "位于moudle-Java中的拦截器初始化了");
    }
}
