package com.sfexpress.sfrouter.service

import com.sfexpress.sfrouter.template.IProvider

import java.lang.reflect.Type

/**
 * json序列化服务
 * Created by sf-zhangpeng on 2018/3/14.
 */
interface SerializationService : IProvider {

    @Deprecated("")
     fun <T> json2Object(input: String, clazz: Class<T>): T

    fun object2Json(instance: Any): String

    fun <T> parseObject(input: String, clazz: Type): T
}
