package com.sfexpress.sfrouter.annotation.models;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 用泛型来获取对应类的classType
 * Created by sf-zhangpeng on 2018/3/9.
 */
public class TypeWrapper<T> {

    private final Type type;

    public TypeWrapper() {
        Type superClass = getClass().getGenericSuperclass();
        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
