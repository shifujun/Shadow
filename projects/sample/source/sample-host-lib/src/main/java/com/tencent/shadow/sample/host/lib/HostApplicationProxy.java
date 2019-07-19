package com.tencent.shadow.sample.host.lib;

import android.content.Context;

/**
 * Created by enzowei on 07/19/2019.
 */
public class HostApplicationProxy implements HostApplicationInterface {
    private static HostApplicationInterface sApp;

    public static void setApp(HostApplicationInterface app) {
        sApp = app;
    }
    public static HostApplicationInterface getApp() {
        return sApp;
    }

    @Override
    public void callPluginService(Context context) {
        if (sApp != null) sApp.callPluginService(context);
    }
}
