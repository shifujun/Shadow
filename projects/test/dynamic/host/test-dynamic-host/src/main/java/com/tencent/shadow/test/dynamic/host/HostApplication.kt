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

package com.tencent.shadow.test.dynamic.host;

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.webkit.WebView
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.dynamic.host.DynamicRuntime
import com.tencent.shadow.dynamic.host.PluginManager
import com.tencent.shadow.test.dynamic.host.manager.Shadow
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.io.File
import kotlin.properties.Delegates

open class HostApplication :
    Application() {
    companion object {
        var sApp: HostApplication by Delegates.notNull()
    }

    var mPluginManager: PluginManager? = null

    val mIdlingResource: SimpleIdlingResourceImpl  = SimpleIdlingResourceImpl()

    override fun onCreate() {
        super.onCreate();
        sApp = this;

        startKoin {
            androidContext(sApp)
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.INFO)
            modules(startModules)
        }

        detectNonSdkApiUsageOnAndroidP();

        LoggerFactory.setILoggerFactory(AndroidLogLoggerFactory())

        //在全动态架构中，Activity组件没有打包在宿主而是位于被动态加载的runtime，
        //为了防止插件crash后，系统自动恢复crash前的Activity组件，此时由于没有加载runtime而发生classNotFound异常，导致二次crash
        //因此这里恢复加载上一次的runtime
        DynamicRuntime.recoveryRuntime(this);

        PluginHelper.getInstance().init(this);

        //Using WebView from more than one process at once with the same data directory is not supported.
        //https://crbug.com/558377
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix(Application.getProcessName());
        }
    }

    private fun detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        val isRunningEspressoTest = kotlin.run {
            try {
                Class.forName("androidx.test.espresso.Espresso");
                true
            } catch (e :Exception) {
                false
            }
        }

        if (isRunningEspressoTest) {
            return;
        }

        val builder = StrictMode.VmPolicy.Builder();
        builder.detectNonSdkApiUsage();
        StrictMode.setVmPolicy(builder.build());
    }

    open fun getApp() : HostApplication {
        return sApp;
    }

    open fun loadPluginManager(apk: File?) {
        if (mPluginManager == null) {
            mPluginManager = Shadow.getPluginManager(apk);
        }
    }

    open fun getPluginManager(): PluginManager? {
        return mPluginManager;
    }

    override fun registerActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks) {

        val pluginFilterCallback = object : ActivityLifecycleCallbacks {

            fun Activity.isPluginContainerActivity(): Boolean {
                return this.javaClass.name.startsWith("com.tencent.shadow.test.dynamic.runtime.container")
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity.isPluginContainerActivity().not()) {
                    callback.onActivityCreated(activity, savedInstanceState)
                }
            }

            override fun onActivityStarted(activity: Activity) {
                if (activity.isPluginContainerActivity().not()) {
                    callback.onActivityStarted(activity)
                }
            }

            override fun onActivityResumed(activity: Activity) {
                if (activity.isPluginContainerActivity().not()) {
                    callback.onActivityResumed(activity)
                }
            }

            override fun onActivityPaused(activity: Activity) {
                if (activity.isPluginContainerActivity().not()) {
                    callback.onActivityPaused(activity)
                }
            }

            override fun onActivityStopped(activity: Activity) {
                if (activity.isPluginContainerActivity().not()) {
                    callback.onActivityStopped(activity)
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                if (activity.isPluginContainerActivity().not()) {
                    callback.onActivitySaveInstanceState(activity, outState)
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (activity.isPluginContainerActivity().not()) {
                    callback.onActivityDestroyed(activity)
                }
            }

        }

        super.registerActivityLifecycleCallbacks(pluginFilterCallback)
    }
}
