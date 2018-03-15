package com.sfexpress.sfrouter.service

import android.net.Uri

import com.sfexpress.sfrouter.template.IProvider

/**
 * 路由url预处理
 * Created by sf-zhangpeng on 2018/3/14.
 */
interface PathReplaceService : IProvider {

    fun forString(path: String): String

    fun forUri(uri: Uri): Uri
}
