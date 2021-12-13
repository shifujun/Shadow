package com.tencent.shadow.test.plugin.androidx_cases.lib

import android.content.ComponentCallbacks2
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tencent.cloudgamepluginbaseactivity.SplashViewModel
import org.koin.android.ext.android.get

class AppCompatTestActivity : AppCompatActivity(), ComponentCallbacks2 {
    val splashViewModel: SplashViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        //  System.out.println("bokeyhuang watch. plugin model=$splashViewModel")
        super.onCreate(savedInstanceState)
        val factory2 = layoutInflater.factory2
        val factory2Class: String
        factory2Class = if (factory2 == null) {
            "null"
        } else {
            factory2.javaClass.name
        }
        val viewGroup = UiUtil.setActivityContentView(this)
        val item = UiUtil.makeItem(
            this,
            "factory2Class",
            "factory2Class",
            factory2Class
        )
        viewGroup.addView(item)
    }
}