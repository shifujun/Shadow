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

package com.tencent.shadow.test.robolectric;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Parcel;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.load_parameters.LoadParameters;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.runtime.container.ContentProviderDelegateProviderHolder;
import com.tencent.shadow.core.runtime.container.DelegateProviderHolder;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class HostApplication extends Application {
    private static Application sApp;

    public final static String PART_MAIN = "partMain";

    static {
        LoggerFactory.setILoggerFactory(AndroidLogLoggerFactory.getInstance());
    }

    private ShadowPluginLoader mPluginLoader;

    private InstalledApk makePluginInHostInstalledApk() {
        LoadParameters loadParameters = new LoadParameters(null,
                PART_MAIN,
                null,
                new String[0]);
        Parcel parcel = Parcel.obtain();
        loadParameters.writeToParcel(parcel, 0);
        ApplicationInfo applicationInfo = getApplicationInfo();
        String apkFilePath = applicationInfo.sourceDir;
        String oDexPath = null;
        String libraryPath = applicationInfo.nativeLibraryDir;
        InstalledApk installedApk = new InstalledApk(apkFilePath,
                oDexPath,
                libraryPath,
                parcel.marshall());
        parcel.recycle();
        return installedApk;
    }

    public void loadPlugin(final String partKey, final Runnable completeRunnable) {
        InstalledApk installedApk = makePluginInHostInstalledApk();
        if (mPluginLoader.getPluginParts(partKey) == null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ShadowPluginLoader pluginLoader = mPluginLoader;
                    Future<?> future = null;
                    try {
                        future = pluginLoader.loadPlugin(installedApk);
                        future.get(10, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("加载失败", e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mPluginLoader.callApplicationOnCreate(partKey);
                    completeRunnable.run();
                }
            }.execute();
        } else {
            completeRunnable.run();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        ShadowPluginLoader loader = mPluginLoader = new TestPluginLoader(getApplicationContext());
        loader.onCreate();
        DelegateProviderHolder.setDelegateProvider(loader.getDelegateProviderKey(), loader);
        ContentProviderDelegateProviderHolder.setContentProviderDelegateProvider(loader);

        Future<?> future = null;
        try {
            InstalledApk installedApk = makePluginInHostInstalledApk();
            future = mPluginLoader.loadPlugin(installedApk);
            future.get(10, TimeUnit.SECONDS);
            mPluginLoader.callApplicationOnCreate(PART_MAIN);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加载失败", e);
        }
    }

    public static Application getApp() {
        return sApp;
    }

    public ShadowPluginLoader getPluginLoader() {
        return mPluginLoader;
    }
}
