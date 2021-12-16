package com.tencent.cloudgamepluginbaseactivity2

import android.content.ComponentCallbacks2
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.tencent.cloudgamepluginbaseactivity.*
import org.koin.android.ext.android.get

open class CloudGameBaseActivity2 : AppCompatActivity(), ComponentCallbacks2,
    Observer<DataResource<GameInfo>> {

    var _splashViewModel: SplashViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        PluginBinding.sPluginMutableLiveDataFactory =
                object : PluginBinding.PluginMutableLiveDataFactory {
                    override fun <T : Any?> build(): PluginMutableLiveData<T> {
                        return PluginMutableLiveDataImpl()
                    }
                }

        val loadGame = _splashViewModel.loadGame("1123123") as MutableLiveData<DataResource<GameInfo>>
        loadGame.observe(this, this)
    }

    override fun onChanged(t: DataResource<GameInfo>?) {
        TODO("Not yet implemented")
    }
}

class PluginMutableLiveDataImpl<T> : MutableLiveData<T>(), PluginMutableLiveData<T>