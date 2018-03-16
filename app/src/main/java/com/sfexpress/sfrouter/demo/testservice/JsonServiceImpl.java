package com.sfexpress.sfrouter.demo.testservice;

import android.content.Context;

import com.google.gson.Gson;
import com.sfexpress.sfrouter.annotation.Route;
import com.sfexpress.sfrouter.service.SerializationService;

import java.lang.reflect.Type;

/**
 * Used for json converter
 */
@Route(path = "/service/json")
public class JsonServiceImpl implements SerializationService {
    private Gson gson = new Gson();

    @Override
    public void init(Context context) {
    }

    @Override
    public <T> T json2Object(String text, Class<T> clazz) {
        return gson.fromJson(text, clazz);
    }

    @Override
    public String object2Json(Object instance) {
        return gson.toJson(instance);
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        return gson.fromJson(input, clazz);
    }
}
