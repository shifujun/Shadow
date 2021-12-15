package com.tencent.cloudgamepluginbaseactivity

import android.content.ComponentCallbacks2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import org.koin.android.ext.android.get

open class CloudGameBaseActivity2 : AppCompatActivity(), ComponentCallbacks2,
    Observer<DataResource<GameInfo>> {

    var _splashViewModel: SplashViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _splashViewModel.loadGame("1123123")?.observe(this, this)
    }

    override fun onChanged(t: DataResource<GameInfo>?) {
        TODO("Not yet implemented")
    }
}