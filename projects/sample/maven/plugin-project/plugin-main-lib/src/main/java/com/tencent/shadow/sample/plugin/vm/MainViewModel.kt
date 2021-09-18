package com.tencent.shadow.sample.plugin.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author:jiyc
 * @date:2021/9/17
 */
class MainViewModel : ViewModel(){
    var mLiveData = MutableLiveData<String>()
}