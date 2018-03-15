package com.sfexpress.sfrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由拦截器的注解
 * 该注解只能标注实现了IRouteInterceptor接口的类，否则会报异常
 * Created by sf-zhangpeng on 2018/3/9.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface RouteInterceptor {

    /**
     * 拦截器的优先级，数字越小优先级越高
     */
    int priority();

    /**
     * 拦截器的名称
     */
    String name() default "Default";
}
