package com.sfexpress.sfrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由页面标记注解
 * Created by sf-zhangpeng on 2018/3/9.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Route {

    /**
     * 路由路径URL
     */
    String path();

    /**
     * 路由分组名称
     */
    String group() default "";

    /**
     * 路由页面名称
     */
    String name() default "undefined";

    /**
     * 自定义参数，用Int作为数据类型，表示32位的设置开关
     */
    int extras() default Integer.MIN_VALUE;

    /**
     * 路由优先级，数字越小优先级越高
     */
    int priority() default -1;
}
