package com.sfexpress.sfrouter.demo.module1;

import android.app.Activity;
import android.os.Bundle;

import com.sfexpress.sfrouter.annotation.Route;

@Route(path = "/module/1")
public class TestModuleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_module);
    }
}
