package com.sfexpress.sfrouter.service

import com.sfexpress.sfrouter.template.IProvider

/**
 * 自动绑定服务
 * Created by sf-zhangpeng on 2018/3/14.
 */
interface AutoBindService : IProvider {

    fun autoBind(instance: Any)
}
