package com.tencent.shadow.test.plugin.androidx_cases.lib

import android.os.Bundle
import com.tencent.cloudgamepluginbaseactivity.DataResource
import com.tencent.cloudgamepluginbaseactivity.GameInfo
import com.tencent.cloudgamepluginbaseactivity2.CloudGameBaseActivity2

class AppCompatTestActivity : CloudGameBaseActivity2() {
   // val splashViewModel: SplashViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("bokeyhuang watch. plugin model=$_splashViewModel")
        val viewGroup = UiUtil.setActivityContentView(this)
        val item = UiUtil.makeItem(
                this,
                "splashViewModel",
                "splashViewModel",
                "bokeyhuang watch. plugin model=$_splashViewModel"
        )
        viewGroup.addView(item)
    }

    override fun onChanged(t: DataResource<GameInfo>?) {
        val message = t?.message
        System.out.println("DataResource.message=$message")
        val viewGroup = UiUtil.setActivityContentView(this)
        val item = UiUtil.makeItem(
                this,
                "message",
                "message",
                "DataResource.message=$message"
        )
        viewGroup.addView(item)
    }
}