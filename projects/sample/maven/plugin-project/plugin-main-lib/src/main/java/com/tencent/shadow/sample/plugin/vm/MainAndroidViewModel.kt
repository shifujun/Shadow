package com.tencent.shadow.sample.plugin.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author:jiyc
 * @date:2021/9/17
 */
class MainAndroidViewModel(application: Application) : AndroidViewModel(application){
    var mLiveData = MutableLiveData<String>()
}