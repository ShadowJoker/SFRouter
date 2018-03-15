package com.sfexpress.sfrouter.demo.kotlin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sfexpress.sfrouter.annotation.Route;

@Route(path = "/kotlin/java")
public class TestNormalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_normal);
    }
}
