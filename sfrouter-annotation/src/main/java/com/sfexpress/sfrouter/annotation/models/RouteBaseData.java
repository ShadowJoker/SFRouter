package com.sfexpress.sfrouter.annotation.models;

import com.sfexpress.sfrouter.annotation.Route;
import com.sfexpress.sfrouter.annotation.enums.RouteTypeEnum;

import java.util.Map;

import javax.lang.model.element.Element;

/**
 * 路由基础数据结构
 * Created by sf-zhangpeng on 2018/3/9.
 */
public class RouteBaseData {

    /**
     * 路由类型
     */
    private RouteTypeEnum type;

    /**
     * Java层原始数据元素
     */
    private Element rawType;

    /**
     * 目的地class
     */
    private Class<?> destination;

    /**
     * 路由URL
     */
    private String path;

    /**
     * 路由分组
     */
    private String group;

    /**
     * 路由参数数据类型map，key是参数名，value是数据类型对应FieldTypeEnum枚举中的序号
     */
    private Map<String, Integer> paramsType;

    /**
     * 路由优先级，数字越小优先级越高
     */
    private int priority = -1;

    /**
     * 自定义参数，用Int作为数据类型，表示32位的设置开关
     */
    private int extra;

    public RouteBaseData() {
    }

    public static RouteBaseData build(RouteTypeEnum type, Class<?> destination, String path, String group, Map<String, Integer> paramsType, int priority, int extra) {
        return new RouteBaseData(type, null, destination, path, group, paramsType, priority, extra);
    }

    public RouteBaseData(Route route, Element rawType, RouteTypeEnum type, Map<String, Integer> paramsType) {
        this(type, rawType, null, route.path(), route.group(), paramsType, route.priority(), route.extras());
    }

    public RouteBaseData(RouteTypeEnum type, Element rawType, Class<?> destination, String path, String group, Map<String, Integer> paramsType, int priority, int extra) {
        this.type = type;
        this.destination = destination;
        this.rawType = rawType;
        this.path = path;
        this.group = group;
        this.priority = priority;
        this.extra = extra;
        this.paramsType = paramsType;
    }

    public Map<String, Integer> getParamsType() {
        return paramsType;
    }

    public RouteBaseData setParamsType(Map<String, Integer> paramsType) {
        this.paramsType = paramsType;
        return this;
    }

    public Element getRawType() {
        return rawType;
    }

    public RouteBaseData setRawType(Element rawType) {
        this.rawType = rawType;
        return this;
    }

    public RouteTypeEnum getType() {
        return type;
    }

    public RouteBaseData setType(RouteTypeEnum type) {
        this.type = type;
        return this;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public RouteBaseData setDestination(Class<?> destination) {
        this.destination = destination;
        return this;
    }

    public String getPath() {
        return path;
    }

    public RouteBaseData setPath(String path) {
        this.path = path;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public RouteBaseData setGroup(String group) {
        this.group = group;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public RouteBaseData setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public int getExtra() {
        return extra;
    }

    public RouteBaseData setExtra(int extra) {
        this.extra = extra;
        return this;
    }

    @Override
    public String toString() {
        return "RouteBaseData{" +
                "type=" + type +
                ", rawType=" + rawType +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", priority=" + priority +
                ", extra=" + extra +
                '}';
    }
}
