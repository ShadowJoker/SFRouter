package com.sfexpress.sfrouter.service

import android.content.Context
import com.sfexpress.sfrouter.core.Postcard

import com.sfexpress.sfrouter.template.IProvider

/**
 * 路由导航失败降级服务
 */
interface DegradeService : IProvider {

    fun onLost(context: Context, postcard: Postcard)
}
