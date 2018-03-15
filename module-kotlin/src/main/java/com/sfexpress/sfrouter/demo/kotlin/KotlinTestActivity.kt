package com.sfexpress.sfrouter.demo.kotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import com.sfexpress.sfrouter.annotation.AutoBind
import com.sfexpress.sfrouter.annotation.Route
import com.sfexpress.sfrouter.launcher.SFRouterManager
import kotlinx.android.synthetic.main.activity_kotlin_test.*

@Route(path = "/kotlin/test")
class KotlinTestActivity : Activity() {

    @AutoBind
    @JvmField
    var name: String? = null
    @AutoBind
    @JvmField
    var age: Int? = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        SFRouterManager.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_test)

        content.text = "name = $name, age = $age"
    }
}
