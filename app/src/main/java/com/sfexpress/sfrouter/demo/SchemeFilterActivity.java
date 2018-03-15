package com.sfexpress.sfrouter.demo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.sfexpress.sfrouter.callback.NavigationCallback;
import com.sfexpress.sfrouter.core.Postcard;
import com.sfexpress.sfrouter.launcher.SFRouterManager;

import org.jetbrains.annotations.NotNull;

public class SchemeFilterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 直接通过ARouter处理外部Uri
        Uri uri = getIntent().getData();
        SFRouterManager.INSTANCE.build(uri).navigation(this, new NavigationCallback() {
            @Override
            public void onInterrupt(@NotNull Postcard postcard) {

            }

            @Override
            public void onLost(@NotNull Postcard postcard) {

            }

            @Override
            public void onFound(@NotNull Postcard postcard) {

            }

            @Override
            public void onArrival(Postcard postcard) {
                finish();
            }
        });
    }
}
