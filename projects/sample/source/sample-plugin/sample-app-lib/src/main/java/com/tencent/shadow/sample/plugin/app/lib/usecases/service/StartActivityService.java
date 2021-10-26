package com.tencent.shadow.sample.plugin.app.lib.usecases.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.tencent.shadow.sample.plugin.app.lib.gallery.util.ToastUtil;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivityOnCreate;

public class StartActivityService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentActivity = new Intent(this, TestActivityOnCreate.class);
        intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentActivity);
        ToastUtil.showToast(this, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
}
