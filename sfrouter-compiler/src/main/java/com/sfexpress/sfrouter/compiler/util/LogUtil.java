package com.sfexpress.sfrouter.compiler.util;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * 日志工具类
 * Created by sf-zhangpeng on 2018/3/9.
 */
public class LogUtil {

    private Messager msg;

    public LogUtil(Messager messager) {
        msg = messager;
    }

    public void i(CharSequence info) {
        if (StringUtils.isNotEmpty(info)) {
            msg.printMessage(Diagnostic.Kind.NOTE, StaticConsts.PREFIX_OF_LOG + info);
        }
    }

    public void w(CharSequence warning) {
        if (StringUtils.isNotEmpty(warning)) {
            msg.printMessage(Diagnostic.Kind.WARNING, StaticConsts.PREFIX_OF_LOG + warning);
        }
    }

    public void e(CharSequence error) {
        if (StringUtils.isNotEmpty(error)) {
            msg.printMessage(Diagnostic.Kind.ERROR, StaticConsts.PREFIX_OF_LOG + "SFRouter发现异常, [" + error + "]");
        }
    }

    public void e(Throwable error) {
        if (null != error) {
            msg.printMessage(Diagnostic.Kind.ERROR, StaticConsts.PREFIX_OF_LOG + "SFRouter发现异常, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()));
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
