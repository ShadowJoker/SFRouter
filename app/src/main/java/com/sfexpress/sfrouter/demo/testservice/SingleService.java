package com.sfexpress.sfrouter.demo.testservice;

import android.content.Context;
import android.widget.Toast;

import com.sfexpress.sfrouter.annotation.Route;
import com.sfexpress.sfrouter.template.IProvider;

/**
 * 测试单类注入
 */
@Route(path = "/service/single")
public class SingleService implements IProvider {

    Context mContext;

    public void sayHello(String name) {
        Toast.makeText(mContext, "Hello " + name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void init(Context context) {
        mContext = context;
    }
}
