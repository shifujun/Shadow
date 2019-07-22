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

package com.tencent.shadow.sample.manager;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.manager.installplugin.InstalledType;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.loader.PluginServiceConnection;
import com.tencent.shadow.dynamic.manager.PluginManagerThatUseDynamicLoader;
import com.tencent.shadow.sample.constant.Constant;
import com.tencent.shadow.sample.plugin.app.lib.IMyAidlInterface;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class FastPluginManager extends PluginManagerThatUseDynamicLoader {

    private static final Logger mLogger = LoggerFactory.getLogger(FastPluginManager.class);

    private ExecutorService mFixedPool = Executors.newFixedThreadPool(4);

    final private Context mCurrentContext;

    public FastPluginManager(Context context) {
        super(context);
        mCurrentContext = context;
    }


    public InstalledPlugin installPlugin(String zip, String hash , boolean odex) throws IOException, JSONException, InterruptedException, ExecutionException {
        final InstalledPlugin installedPlugin = installPluginFromZip(new File(zip), hash);

        List<Future> futures = new LinkedList<>();
        if (installedPlugin.runtimeFile != null && installedPlugin.pluginLoaderFile != null) {
            Future odexRuntime = mFixedPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    oDexPluginLoaderOrRunTime(installedPlugin.UUID, InstalledType.TYPE_PLUGIN_RUNTIME);
                    return null;
                }
            });
            futures.add(odexRuntime);
            Future odexLoader = mFixedPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    oDexPluginLoaderOrRunTime(installedPlugin.UUID, InstalledType.TYPE_PLUGIN_LOADER);
                    return null;
                }
            });
            futures.add(odexLoader);
        }
        for (Map.Entry<String, InstalledPlugin.PluginPart> plugin : installedPlugin.plugins.entrySet()) {
            final String partKey = plugin.getKey();
            Future extractSo = mFixedPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    extractSo(installedPlugin.UUID, partKey);
                    return null;
                }
            });
            futures.add(extractSo);
            if (odex) {
                Future odexPlugin = mFixedPool.submit(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        oDexPlugin(installedPlugin.UUID, partKey);
                        return null;
                    }
                });
                futures.add(odexPlugin);
            }
        }

        for (Future future : futures) {
            future.get();
        }
        return getInstalledPlugins(1).get(0);
    }


    public void startPluginActivity(Context context, InstalledPlugin installedPlugin, String partKey, Intent pluginIntent) throws RemoteException, TimeoutException, FailedException {
        Intent intent = convertActivityIntent(installedPlugin, partKey, pluginIntent);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public Intent convertActivityIntent(InstalledPlugin installedPlugin, String partKey, Intent pluginIntent) throws RemoteException, TimeoutException, FailedException {
        loadPlugin(installedPlugin.UUID, partKey);
        Map map = mPluginLoader.getLoadedPlugin();
        Boolean isCall = (Boolean) map.get(partKey);
        if (isCall == null || !isCall) {
            mPluginLoader.callApplicationOnCreate(partKey);
        }
        return mPluginLoader.convertActivityIntent(pluginIntent);
    }

    private void loadPluginLoaderAndRuntime(String uuid) throws RemoteException, TimeoutException, FailedException {
        if (mPpsController == null) {
            bindPluginProcessService(getPluginProcessServiceName());
            waitServiceConnected(10, TimeUnit.SECONDS);
        }
        loadRunTime(uuid);
        loadPluginLoader(uuid);
    }

    private void loadPlugin(String uuid, String partKey) throws RemoteException, TimeoutException, FailedException {
        loadPluginLoaderAndRuntime(uuid);
        Map map = mPluginLoader.getLoadedPlugin();
        if (!map.containsKey(partKey)) {
            mPluginLoader.loadPlugin(partKey);
        }
    }


    protected abstract String getPluginProcessServiceName();

    void onStartActivity(final Context context, Bundle bundle, final EnterCallback callback) {
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY);
        final String className = bundle.getString(Constant.KEY_ACTIVITY_CLASSNAME);
        if (className == null) {
            throw new NullPointerException("className == null");
        }
        final Bundle extras = bundle.getBundle(Constant.KEY_EXTRAS);

        if (callback != null) {
            final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.activity_load_plugin, null);
            callback.onShowLoadingView(view);
        }

        mFixedPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InstalledPlugin installedPlugin = installPlugin(pluginZipPath, null, true);
                    Intent pluginIntent = new Intent();
                    pluginIntent.setClassName(
                            context.getPackageName(),
                            className
                    );
                    if (extras != null) {
                        pluginIntent.replaceExtras(extras);
                    }

                    startPluginActivity(context, installedPlugin, partKey, pluginIntent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (callback != null) {
                    callback.onCloseLoadingView();
                }
            }
        });
    }

    void callPluginService(final Context context, Bundle bundle) {
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY);
        final String className = bundle.getString(Constant.KEY_ACTIVITY_CLASSNAME);

        Intent pluginIntent = new Intent();
        pluginIntent.setClassName(context.getPackageName(), className);

        mFixedPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InstalledPlugin installedPlugin
                            = installPlugin(pluginZipPath, null, true);//这个调用是阻塞的

                    loadPlugin(installedPlugin.UUID, partKey);

                    Intent pluginIntent = new Intent();
                    pluginIntent.setClassName(context.getPackageName(), className);

                    boolean callSuccess = mPluginLoader.bindPluginService(pluginIntent, new PluginServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                            IMyAidlInterface iMyAidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);
                            try {
                                String s = iMyAidlInterface.basicTypes(1, 2, true, 4.0f, 5.0, "6");
                                Log.d("SamplePluginManager", "call basicTypes at process:" + Process.myPid());
                                Log.i("SamplePluginManager", "iMyAidlInterface.basicTypes : " + s);
//                                Toast.makeText(context, "iMyAidlInterface.basicTypes : " + s, Toast.LENGTH_LONG).show();
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName componentName) {
                            throw new RuntimeException("onServiceDisconnected");
                        }
                    }, Service.BIND_AUTO_CREATE);

                    if (!callSuccess) {
                        throw new RuntimeException("bind service失败 className==" + className);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
