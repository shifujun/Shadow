package com.tencent.shadow.sample.plugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.tencent.shadow.sample.plugin.vm.MainAndroidViewModel
import com.tencent.shadow.sample.plugin.vm.MainViewModel

class MainActivity : AppCompatActivity() {
    /**
     * 1.插件单独运行时，以下两种viewModel都可正常工作;
     * 2.release环境下：
     *                  a>宿主启动当前activity后，liveData工作不正常;
     *                  b>根据报错信息来看：方式二的viewModel因继承AndroidViewModel(),不能正常创建实例。
     */

    //方式一viewModel继承ViewModel()：
    private val mViewModel: MainViewModel by viewModels()

    //方式二viewModel继承AndroidViewModel()：
    //private val mViewModel: MainAndroidViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tv).setOnClickListener {
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
            mViewModel.mLiveData.value = "THIS IS PLUGIN"
        }

        mViewModel.mLiveData.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            findViewById<TextView>(R.id.tv).text = it
        })
    }
}