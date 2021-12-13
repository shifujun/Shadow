package com.tencent.cloudgamepluginbaseactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tencent.cloudgamepluginbaseactivity.component.ParameterContext

open class CloudGameBaseActivity : AppCompatActivity() {

    lateinit var _splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _splashViewModel = ParameterContext.getInstance()._splashViewModel
    }
}