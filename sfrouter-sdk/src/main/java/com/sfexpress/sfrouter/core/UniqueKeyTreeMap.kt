package com.sfexpress.sfrouter.core

import java.util.*

/**
 * 唯一key的TreeMap数据结构，用来存储和管理拦截器，key是拦截器的优先级
 * Created by sf-zhangpeng on 2018/3/13.
 */
class UniqueKeyTreeMap<K, V>(private val exceptionText: String) : TreeMap<K, V>() {

    override fun put(key: K, value: V): V? {
        return if (containsKey(key)) {
            throw RuntimeException(String.format(exceptionText, key))
        } else {
            super.put(key, value)
        }
    }
}