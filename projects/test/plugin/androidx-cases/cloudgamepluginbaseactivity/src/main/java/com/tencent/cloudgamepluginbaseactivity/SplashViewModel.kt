/**
 * SplashViewModel.kt
 * @author blinkjiang
 * @date 2019-07-17
 */
package com.tencent.cloudgamepluginbaseactivity

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SplashViewModel(
    val applicationContext: Context,
) : ViewModel() {

    fun loadGame(gameId: String): LiveData<DataResource<GameInfo>>? {
        return null
    }
}
