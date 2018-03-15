package com.sfexpress.sfrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 页面跳转参数自动绑定注解
 * Created by sf-zhangpeng on 2018/3/8.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface AutoBind {

    /**
     * 参数的名字，默认就是代码中的变量名
     */
    String name() default "";

    /**
     * 标记该字段是否是必传字段，如果是必传，当该参数为null时会报错。
     */
    boolean required() default false;
}
