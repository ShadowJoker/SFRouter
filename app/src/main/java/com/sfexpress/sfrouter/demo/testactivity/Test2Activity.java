package com.sfexpress.sfrouter.demo.testactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.sfexpress.sfrouter.demo.R;
import com.sfexpress.sfrouter.annotation.AutoBind;
import com.sfexpress.sfrouter.annotation.Route;
import com.sfexpress.sfrouter.launcher.SFRouterManager;

@Route(path = "/test/activity2")
public class Test2Activity extends AppCompatActivity {

    @AutoBind
    String key1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test2);

        SFRouterManager.INSTANCE.inject(this);

        if (!TextUtils.isEmpty(key1)) {
            Toast.makeText(this, "携带参数 :" + key1, Toast.LENGTH_LONG).show();
        }
//        String value = getIntent().getStringExtra("key1");
//        if (!TextUtils.isEmpty(value)) {
//            Toast.makeText(this, "exist param :" + value, Toast.LENGTH_LONG).show();
//        }

        setResult(999);
    }
}
