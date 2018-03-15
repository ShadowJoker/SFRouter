package com.sfexpress.sfrouter.demo.testservice;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sfexpress.sfrouter.annotation.Route;

@Route(path = "/service/hello")
public class HelloServiceImpl implements HelloService {
    Context mContext;

    @Override
    public void sayHello(String name) {
        Toast.makeText(mContext, "Hello " + name, Toast.LENGTH_SHORT).show();
    }

    /**
     * Do your init work in this method, it well be call when processor has been load.
     *
     * @param context ctx
     */
    @Override
    public void init(Context context) {
        mContext = context;
        Log.e("testService", HelloService.class.getName() + " has init.");
    }
}
