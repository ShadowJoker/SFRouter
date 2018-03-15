package com.sfexpress.sfrouter.annotation.enums;

/**
 * 路由类型枚举
 * Created by sf-zhangpeng on 2018/3/9.
 */
public enum RouteTypeEnum {
    ACTIVITY(0, "android.app.Activity"),
    SERVICE(1, "android.app.Service"),
    PROVIDER(2, "com.sfexpress.sfrouter.template.IProvider"),
    UNKNOWN(-1, "Unknown route type");

    private int id;
    private String className;

    RouteTypeEnum(int id, String className) {
        this.id = id;
        this.className = className;
    }

    /**
     * 通过类名获取对应的RouteTypeEnum
     */
    public static RouteTypeEnum parse(String name) {
        for (RouteTypeEnum routeType : RouteTypeEnum.values()) {
            if (routeType.getClassName().equals(name)) {
                return routeType;
            }
        }
        return UNKNOWN;
    }

    public int getId() {
        return id;
    }

    public RouteTypeEnum setId(int id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public RouteTypeEnum setClassName(String className) {
        this.className = className;
        return this;
    }
}
