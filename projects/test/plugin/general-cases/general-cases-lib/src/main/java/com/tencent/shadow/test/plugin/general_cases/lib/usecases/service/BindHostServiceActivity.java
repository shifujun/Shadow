package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class BindHostServiceActivity extends Activity {
    private ServiceConnection connectedService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent bindPPS = new Intent();
        bindPPS.setClassName(this, "com.tencent.shadow.test.dynamic.host.PluginProcessPPS");

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                connectedService = this;
                finish();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        boolean success = bindService(bindPPS, serviceConnection, BIND_AUTO_CREATE);

        if (!success) {
            throw new RuntimeException("绑定服务失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectedService != null) {
            unbindService(connectedService);
        }
    }
}
