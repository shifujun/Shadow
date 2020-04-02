package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SystemExitService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder() {
            @Override
            protected boolean onTransact(int code,
                                         @NonNull Parcel data,
                                         @Nullable Parcel reply,
                                         int flags) throws RemoteException {
                //随便发什么来都退出进程以便触发onServiceDisconnected
                System.exit(0);
                return super.onTransact(code, data, reply, flags);
            }
        };
    }
}
