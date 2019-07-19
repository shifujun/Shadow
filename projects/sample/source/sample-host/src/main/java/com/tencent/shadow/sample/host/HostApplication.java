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

package com.tencent.shadow.sample.host;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.dynamic.host.DynamicRuntime;
import com.tencent.shadow.dynamic.host.PluginManager;
import com.tencent.shadow.sample.constant.Constant;
import com.tencent.shadow.sample.host.lib.HostApplicationInterface;
import com.tencent.shadow.sample.host.lib.HostApplicationProxy;
import com.tencent.shadow.sample.host.lib.HostUiLayerProvider;
import com.tencent.shadow.sample.host.lib.PluginHelper;
import com.tencent.shadow.sample.host.manager.Shadow;

import java.io.File;

public class HostApplication extends Application implements HostApplicationInterface {
    private static HostApplication sApp;

    private PluginManager mPluginManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        HostApplicationProxy.setApp(this);
        detectNonSdkApiUsageOnAndroidP();

        LoggerFactory.setILoggerFactory(new AndroidLogLoggerFactory());

        //在全动态架构中，Activity组件没有打包在宿主而是位于被动态加载的runtime，
        //为了防止插件crash后，系统自动恢复crash前的Activity组件，此时由于没有加载runtime而发生classNotFound异常，导致二次crash
        //因此这里恢复加载上一次的runtime
        DynamicRuntime.recoveryRuntime(this);

        PluginHelper.getInstance().init(this);

        HostUiLayerProvider.init(this);
    }

    private static void detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        builder.penaltyDeath();
        builder.detectNonSdkApiUsage();
        StrictMode.setVmPolicy(builder.build());
    }

    public static HostApplication getApp() {
        return sApp;
    }

    public void loadPluginManager(File apk) {
        if (mPluginManager == null) {
            mPluginManager = Shadow.getPluginManager(apk);
        }
    }

    public PluginManager getPluginManager() {
        return mPluginManager;
    }

    @Override
    public void callPluginService(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, PluginHelper.getInstance().pluginZipFile.getAbsolutePath());
        bundle.putString(Constant.KEY_PLUGIN_PART_KEY, "sample-plugin-app");
        bundle.putString(Constant.KEY_ACTIVITY_CLASSNAME, "com.tencent.shadow.sample.plugin.app.lib.gallery.MyService");
        loadPluginManager(PluginHelper.getInstance().pluginManagerFile);
        getPluginManager().enter(context, Constant.FROM_ID_CALL_SERVICE, bundle, null);
    }
}
