package com.tencent.shadow.test.plugin.androidx_cases.lib

import android.content.ComponentCallbacks2
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tencent.cloudgamepluginbaseactivity.SplashViewModel
import org.koin.android.ext.android.get

class AppCompatTestActivity : AppCompatActivity(), ComponentCallbacks2 {
    val splashViewModel: SplashViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("bokeyhuang watch. plugin model=$splashViewModel")
        val viewGroup = UiUtil.setActivityContentView(this)
        val item = UiUtil.makeItem(
            this,
            "splashViewModel",
            "splashViewModel",
            "bokeyhuang watch. plugin model=$splashViewModel"
        )
        viewGroup.addView(item)
    }
}