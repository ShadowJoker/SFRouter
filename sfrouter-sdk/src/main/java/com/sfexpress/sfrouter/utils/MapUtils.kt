package com.sfexpress.sfrouter.utils

/**
 * Map判空工具类
 * Created by sf-zhangpeng on 2018/3/13.
 */
object MapUtils {

    fun isEmpty(map: Map<*, *>?): Boolean {
        return map == null || map.isEmpty()
    }

    fun isNotEmpty(map: Map<*, *>): Boolean {
        return !isEmpty(map)
    }
}
