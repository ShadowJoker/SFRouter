package com.sfexpress.sfrouter.demo.testservice;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.sfexpress.sfrouter.annotation.Route;
import com.sfexpress.sfrouter.service.SerializationService;

import java.lang.reflect.Type;

/**
 * Used for json converter
 */
@Route(path = "/service/json")
public class JsonServiceImpl implements SerializationService {
    @Override
    public void init(Context context) {
    }

    @Override
    public <T> T json2Object(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    @Override
    public String object2Json(Object instance) {
        return JSON.toJSONString(instance);
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        return JSON.parseObject(input, clazz);
    }
}
