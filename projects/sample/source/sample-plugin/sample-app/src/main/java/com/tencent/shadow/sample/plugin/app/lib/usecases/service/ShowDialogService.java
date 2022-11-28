package com.tencent.shadow.sample.plugin.app.lib.usecases.service;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.tencent.shadow.sample.host.lib.ObjectHolder;

public class ShowDialogService extends IntentService {
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public ShowDialogService() {
        super("ShowDialogService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        uiHandler.post(() -> {
            //从公共接口中取出对象，可以强制转换为公共类型Context
            Context mainActivityInHost = (Context) ObjectHolder.map.remove("MainActivity.this");

            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivityInHost);
            builder.setMessage("show from ShowDialogService");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

}