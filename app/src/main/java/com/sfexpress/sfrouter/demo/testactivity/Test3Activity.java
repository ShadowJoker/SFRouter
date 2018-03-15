package com.sfexpress.sfrouter.demo.testactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sfexpress.sfrouter.demo.R;
import com.sfexpress.sfrouter.annotation.AutoBind;
import com.sfexpress.sfrouter.annotation.Route;
import com.sfexpress.sfrouter.launcher.SFRouterManager;

/**
 * 自动注入的测试用例
 */
@Route(path = "/test/activity3")
public class Test3Activity extends AppCompatActivity {

    @AutoBind
    String name;

    @AutoBind
    int age;

    @AutoBind(name = "boy")
    boolean girl;

    // 这个字段没有注解，是不会自动注入的
    private long high;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        SFRouterManager.INSTANCE.inject(this);

        String params = String.format("name=%s, age=%s, girl=%s, high=%s", name, age, girl, high);

        ((TextView) findViewById(R.id.test)).setText("I am " + Test3Activity.class.getName());
        ((TextView) findViewById(R.id.test2)).setText(params);
    }
}
