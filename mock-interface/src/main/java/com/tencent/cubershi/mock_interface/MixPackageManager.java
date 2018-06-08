package com.tencent.cubershi.mock_interface;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MixPackageManager extends PackageManagerWrapper {
    final private PackageManager mPluginPackageManager;

    public MixPackageManager(PackageManager mHostPackageManager, PackageManager mPluginPackageManager) {
        super(mHostPackageManager);
        this.mPluginPackageManager = mPluginPackageManager;
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws NameNotFoundException {
        final ApplicationInfo applicationInfo = super.getApplicationInfo(packageName, flags);
        final ApplicationInfo pluginAppInfo = mPluginPackageManager.getApplicationInfo(packageName, flags);
        if (pluginAppInfo.metaData != null) {
            if (applicationInfo.metaData == null) {
                applicationInfo.metaData = new Bundle();
            }
            applicationInfo.metaData.putAll(pluginAppInfo.metaData);
        }
        return applicationInfo;
    }
}