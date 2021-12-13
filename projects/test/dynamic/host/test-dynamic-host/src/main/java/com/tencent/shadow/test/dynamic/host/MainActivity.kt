/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.tencent.shadow.test.dynamic.host

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tencent.cloudgamepluginbaseactivity.SplashViewModel
import com.tencent.shadow.test.lib.constant.Constant
import com.tencent.shadow.test.lib.custom_view.TestViewConstructorCacheView
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    val splashViewModel : SplashViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("bokeyhuang watch. host model=$splashViewModel")
        setTheme(R.style.TestHostTheme)
        val rootView = LinearLayout(this)
        rootView.orientation = LinearLayout.VERTICAL
        val infoTextView = TextView(this)
        infoTextView.setText(R.string.main_activity_info)
        rootView.addView(infoTextView)
        val partKeySpinner = Spinner(this)
        val partKeysAdapter = ArrayAdapter<String>(this, R.layout.part_key_adapter)
        partKeysAdapter.addAll(
            Constant.PART_KEY_PLUGIN_ANDROIDX
        )
        partKeySpinner.adapter = partKeysAdapter
        rootView.addView(partKeySpinner)
        val startPluginButton = Button(this)
        startPluginButton.setText(R.string.start_plugin)
        startPluginButton.setOnClickListener {
            val partKey = partKeySpinner.selectedItem as String
            val intent = Intent(
                this@MainActivity,
                PluginLoadActivity::class.java
            )
            intent.putExtra(
                Constant.KEY_PLUGIN_PART_KEY,
                partKey
            )
            when (partKey) {
                Constant.PART_KEY_PLUGIN_ANDROIDX -> intent.putExtra(
                    Constant.KEY_ACTIVITY_CLASSNAME,
                    "com.tencent.shadow.test.plugin.androidx_cases.lib.AppCompatTestActivity"
                )
            }
            startActivity(intent)
        }
        rootView.addView(startPluginButton)
        rootView.addView(TestViewConstructorCacheView(this))
        setContentView(rootView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (permissions[i] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        throw RuntimeException("必须赋予权限.")
                    }
                }
            }
        }
    }
}