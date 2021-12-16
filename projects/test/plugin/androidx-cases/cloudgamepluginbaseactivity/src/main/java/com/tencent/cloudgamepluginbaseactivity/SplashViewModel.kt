/**
 * SplashViewModel.kt
 * @author blinkjiang
 * @date 2019-07-17
 */
package com.tencent.cloudgamepluginbaseactivity

import android.content.Context
import androidx.lifecycle.ViewModel
import com.tencent.cloudgamepluginbaseactivity.PluginBinding.sPluginMutableLiveDataFactory

class SplashViewModel(
    val applicationContext: Context,
) : ViewModel() {

    fun loadGame(gameId: String): PluginMutableLiveData<DataResource<GameInfo>>? {
        val pluginMutableLiveData = sPluginMutableLiveDataFactory.build<DataResource<GameInfo>>()
        pluginMutableLiveData.setValue(DataResource.error(gameId))
        return pluginMutableLiveData
    }
}
