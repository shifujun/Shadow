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

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import com.tencent.shadow.dynamic.host.EnterCallback
import com.tencent.shadow.test.lib.constant.Constant

class PluginLoadActivity : Activity() {
    private var mViewGroup: ViewGroup? = null
    private val mHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)
        mViewGroup = findViewById(R.id.container)
        startPlugin()
    }

    fun startPlugin() {
        PluginHelper.getInstance().singlePool.execute {
            HostApplication().getApp()
                .loadPluginManager(PluginHelper.getInstance().pluginManagerFile)
            val bundle = Bundle()
            bundle.putString(
                Constant.KEY_PLUGIN_ZIP_PATH,
                PluginHelper.getInstance().pluginZipFile.absolutePath
            )
            bundle.putString(
                Constant.KEY_PLUGIN_PART_KEY,
                intent.getStringExtra(Constant.KEY_PLUGIN_PART_KEY)
            )
            bundle.putString(
                Constant.KEY_ACTIVITY_CLASSNAME,
                intent.getStringExtra(Constant.KEY_ACTIVITY_CLASSNAME)
            )
            HostApplication().getApp().getPluginManager()
                ?.enter(
                    this@PluginLoadActivity,
                    Constant.FROM_ID_START_ACTIVITY.toLong(),
                    bundle,
                    object : EnterCallback {
                        override fun onShowLoadingView(view: View) {
                            mHandler.post { mViewGroup!!.addView(view) }
                        }

                        override fun onCloseLoadingView() {
                            finish()
                        }

                        override fun onEnterComplete() {}
                    })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewGroup!!.removeAllViews()
    }
}