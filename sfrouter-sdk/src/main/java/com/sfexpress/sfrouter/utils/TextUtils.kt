package com.sfexpress.sfrouter.utils

import android.net.Uri
import java.util.*

/**
 * 文字处理工具类
 * Created by sf-zhangpeng on 2018/3/13.
 */
object TextUtils {

    /**
     * 打印线程的栈信息
     */
    fun formatStackTrace(stackTrace: Array<StackTraceElement>): String {
        val sb = StringBuilder()
        for (element in stackTrace) {
            sb.append("    at ").append(element.toString())
            sb.append("\n")
        }
        return sb.toString()
    }

    /**
     * 拆分uri中的参数
     */
    fun splitQueryParameters(rawUri: Uri): Map<String, String> {
        val query = rawUri.encodedQuery ?: return emptyMap()

        val paramMap = LinkedHashMap<String, String>()
        var start = 0
        do {
            val next = query.indexOf('&', start)
            val end = if (next == -1) query.length else next

            var separator = query.indexOf('=', start)
            if (separator > end || separator == -1) {
                separator = end
            }

            val name = query.substring(start, separator)

            if (!android.text.TextUtils.isEmpty(name)) {
                val value = if (separator == end) "" else query.substring(separator + 1, end)
                paramMap[Uri.decode(name)] = Uri.decode(value)
            }
            // 将计数指针移到最后
            start = end + 1
        } while (start < query.length)

        return Collections.unmodifiableMap(paramMap)
    }
}
