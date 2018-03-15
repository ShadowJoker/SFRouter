package com.sfexpress.sfrouter.template

import android.content.Context

/**
 * provider模板，用于自定义service场景
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface IProvider {

    /**
     * 初始化逻辑，在编译期处理器执行时会调用
     */
    fun init(context: Context)
}
