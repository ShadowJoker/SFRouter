package com.sfexpress.sfrouter.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sfexpress.sfrouter.callback.NavigationCallback;
import com.sfexpress.sfrouter.core.Postcard;
import com.sfexpress.sfrouter.demo.testinject.TestObj;
import com.sfexpress.sfrouter.demo.testinject.TestParcelable;
import com.sfexpress.sfrouter.demo.testservice.HelloService;
import com.sfexpress.sfrouter.demo.testservice.SingleService;
import com.sfexpress.sfrouter.launcher.SFRouterManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SFRouterManager.INSTANCE.init(getApplication());
        activity = this;
    }

    public static Activity getThis() {
        return activity;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.normalNavigation:
                SFRouterManager.INSTANCE
                        .build("/test/activity2")
                        .navigation();
                break;
            case R.id.kotlinNavigation:
                SFRouterManager.INSTANCE
                        .build("/kotlin/test")
                        .withString("name", "老王")
                        .withInt("age", 23)
                        .navigation();
                break;
            case R.id.normalNavigationWithParams:
                Uri testUriMix = Uri.parse("arouter://na.sfexpress.com/test/activity2");
                SFRouterManager.INSTANCE.build(testUriMix)
                        .withString("key1", "value1")
                        .navigation();
                break;
            case R.id.oldVersionAnim:
                SFRouterManager.INSTANCE
                        .build("/test/activity2")
                        .withTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .navigation(this);
                break;
            case R.id.newVersionAnim:
                if (Build.VERSION.SDK_INT >= 16) {
                    SFRouterManager.INSTANCE
                            .build("/test/activity2")
                            .navigation();
                } else {
                    Toast.makeText(this, "API < 16,不支持新版本动画", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.interceptor:
                SFRouterManager.INSTANCE
                        .build("/test/activity4")
                        .navigation(this, new NavigationCallback() {
                            @Override
                            public void onLost(@NotNull Postcard postcard) {
                            }

                            @Override
                            public void onFound(@NotNull Postcard postcard) {
                            }

                            @Override
                            public void onArrival(Postcard postcard) {

                            }

                            @Override
                            public void onInterrupt(Postcard postcard) {
                                Log.d("ARouter", "被拦截了");
                            }
                        });
                break;
            case R.id.navByUrl:
                SFRouterManager.INSTANCE
                        .build("/test/webview")
                        .withString("url", "file:///android_asset/schame-test.html")
                        .navigation();
                break;
            case R.id.autoInject:
                TestParcelable testParcelable = new TestParcelable("jack", 666);
                TestObj testObj = new TestObj("Rose", 777);
                List<TestObj> objList = new ArrayList<>();
                objList.add(testObj);

                Map<String, List<TestObj>> map = new HashMap<>();
                map.put("testMap", objList);

                SFRouterManager.INSTANCE.build("/test/activity1")
                        .withString("name", "老王")
                        .withInt("age", 18)
                        .withBoolean("boy", true)
                        .withLong("high", 180)
                        .withString("url", "https://a.b.c")
                        .withParcelable("pac", testParcelable)
                        .withObject("obj", testObj)
                        .withObject("objList", objList)
                        .withObject("map", map)
                        .navigation();
                break;
            case R.id.navByName:
                ((HelloService) SFRouterManager.INSTANCE.build("/service/hello").navigation()).sayHello("mike");
                break;
            case R.id.navByType:
                SFRouterManager.INSTANCE.navigation(HelloService.class).sayHello("mike");
                break;
            case R.id.navToMoudle1:
                SFRouterManager.INSTANCE.build("/module/1").navigation();
                break;
            case R.id.navToMoudle2:
                // 这个页面主动指定了Group名
                SFRouterManager.INSTANCE.build("/module/2", "m2").navigation();
                break;
            case R.id.failNav:
                SFRouterManager.INSTANCE.build("/xxx/xxx").navigation(this, new NavigationCallback() {
                    @Override
                    public void onFound(Postcard postcard) {
                        Log.d("ARouter", "找到了");
                    }

                    @Override
                    public void onLost(Postcard postcard) {
                        Log.d("ARouter", "找不到了");
                    }

                    @Override
                    public void onArrival(Postcard postcard) {
                        Log.d("ARouter", "跳转完了");
                    }

                    @Override
                    public void onInterrupt(Postcard postcard) {
                        Log.d("ARouter", "被拦截了");
                    }
                });
                break;
            case R.id.callSingle:
                SFRouterManager.INSTANCE.navigation(SingleService.class).sayHello("Mike");
                break;
            case R.id.failNav2:
                SFRouterManager.INSTANCE.build("/xxx/xxx").navigation();
                break;
            case R.id.failNav3:
                SFRouterManager.INSTANCE.navigation(MainActivity.class);
                break;
            case R.id.normalNavigation2:
                SFRouterManager.INSTANCE
                        .build("/test/activity2")
                        .navigation(this, 666);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 666:
                Log.e("activityResult", String.valueOf(resultCode));
                break;
            default:
                break;
        }
    }
}
