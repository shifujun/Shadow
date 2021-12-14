package com.tencent.cloudgamepluginbaseactivity

import android.content.ComponentCallbacks2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.android.ext.android.get

open class CloudGameBaseActivity : AppCompatActivity(), ComponentCallbacks2 {

    var _splashViewModel: SplashViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}