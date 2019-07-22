package com.tencent.shadow.sample.manager;

import android.content.Context;
import android.os.Bundle;

import com.tencent.shadow.dynamic.host.EnterCallback;

class PluginManager1 extends FastPluginManager {
    PluginManager1(Context context) {
        super(context);
    }

    @Override
    protected String getPluginProcessServiceName() {
        return "com.tencent.shadow.sample.host.PluginProcessPPS";
    }

    @Override
    protected String getName() {
        return "PluginManager1";
    }

    @Override
    public void enter(final Context context, long fromId, Bundle bundle, final EnterCallback callback) {

    }
}
