package com.tencent.shadow.test.plugin.particular_cases.plugin_service_for_host;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

@SuppressWarnings("NullableProblems")
public class CreateHandlerTestService extends Service {

    public CreateHandlerTestService() {
        new Handler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder() {
            @Override
            protected boolean onTransact(int code,
                                         Parcel data,
                                         Parcel reply,
                                         int flags) throws RemoteException {
                return super.onTransact(code, data, reply, flags);
            }
        };

    }

}
