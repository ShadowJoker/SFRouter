package com.sfexpress.sfrouter.template

import com.sfexpress.sfrouter.core.UniqueKeyTreeMap
import java.util.*

/**
 * 拦截器group的模板，用来载入拦截器
 * Created by sf-zhangpeng on 2018/3/13.
 */
interface IInterceptorGroup {

    fun loadInto(interceptor: TreeMap<Int, Class<out IInterceptor>>)
}
