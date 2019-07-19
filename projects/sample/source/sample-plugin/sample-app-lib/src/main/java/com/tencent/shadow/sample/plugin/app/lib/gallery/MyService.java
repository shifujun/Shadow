package com.tencent.shadow.sample.plugin.app.lib.gallery;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.os.Process;
import com.tencent.shadow.sample.plugin.app.lib.IMyAidlInterface;

/**
 * Created by enzowei on 07/19/2019.
 */

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new IMyAidlInterface.Stub() {
            @Override
            public String basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
                Log.d("MyService", "call basicTypes at process:"+Process.myPid());
                return Integer.toString(anInt) + aLong + aBoolean + aFloat + aDouble + aString;
            }
        };
    }
}