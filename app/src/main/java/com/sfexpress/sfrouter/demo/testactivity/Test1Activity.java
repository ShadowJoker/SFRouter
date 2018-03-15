package com.sfexpress.sfrouter.demo.testactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sfexpress.sfrouter.annotation.AutoBind;
import com.sfexpress.sfrouter.annotation.Route;
import com.sfexpress.sfrouter.demo.R;
import com.sfexpress.sfrouter.demo.testinject.TestObj;
import com.sfexpress.sfrouter.demo.testinject.TestParcelable;
import com.sfexpress.sfrouter.demo.testservice.HelloService;
import com.sfexpress.sfrouter.launcher.SFRouterManager;

import java.util.List;
import java.util.Map;

/**
 * https://na.sfexpress.com/test/activity1?name=老王&age=23&boy=true&high=180
 */
@Route(path = "/test/activity1")
public class Test1Activity extends AppCompatActivity {

    @AutoBind
    String name = "jack";

    @AutoBind
    int age = 10;

    @AutoBind
    int height = 175;

    @AutoBind(name = "boy")
    boolean girl;

    @AutoBind
    char ch = 'A';

    @AutoBind
    float fl = 12.00f;

    @AutoBind
    double dou = 12.01d;

    @AutoBind
    TestParcelable pac;

    @AutoBind
    TestObj obj;

    @AutoBind
    List<TestObj> objList;

    @AutoBind
    Map<String, List<TestObj>> map;

    private long high;

    @AutoBind
    String url;

    @AutoBind
    HelloService helloService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        SFRouterManager.INSTANCE.inject(this);

        // No more getter ...
        // name = getIntent().getStringExtra("name");
        // age = getIntent().getIntExtra("age", 0);
        // girl = getIntent().getBooleanExtra("girl", false);
        // high = getIntent().getLongExtra("high", 0);
        // url = getIntent().getStringExtra("url");

        String params = String.format(
                "name=%s,\n age=%s, \n height=%s,\n girl=%s,\n high=%s,\n url=%s,\n pac=%s,\n obj=%s \n ch=%s \n fl = %s, \n dou = %s, \n objList=%s, \n map=%s",
                name,
                age,
                height,
                girl,
                high,
                url,
                pac,
                obj,
                ch,
                fl,
                dou,
                objList,
                map
        );
        helloService.sayHello("Hello 老司机.");

        ((TextView) findViewById(R.id.test)).setText("I am " + Test1Activity.class.getName());
        ((TextView) findViewById(R.id.test2)).setText(params);
    }
}
