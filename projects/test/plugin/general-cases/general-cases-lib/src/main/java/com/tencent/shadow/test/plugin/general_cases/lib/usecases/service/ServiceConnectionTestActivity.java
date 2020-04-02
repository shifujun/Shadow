package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.WithIdlingResourceActivity;

/**
 * ServiceConnection回调测试
 * <p>
 * 点bindService按钮，预期收到onServiceConnected调用，显示出回调信息。
 * 再点stopService按钮，预期SystemExitService自己杀进程，然后收到onServiceDisconnected调用，显示出回调信息。
 */
public class ServiceConnectionTestActivity extends WithIdlingResourceActivity {

    private static final String STATUS_VIEW_TAG = "STATUS_VIEW_TAG";
    private static final String PACKAGE_VIEW_TAG = "PACKAGE_VIEW_TAG";
    private static final String CLASS_VIEW_TAG = "CLASS_VIEW_TAG";

    private ViewGroup viewGroup;
    private IBinder service;

    final private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceConnectionTestActivity.this.service = service;
            UiUtil.setItemValue(viewGroup, STATUS_VIEW_TAG, "onServiceConnected");
            UiUtil.setItemValue(viewGroup, PACKAGE_VIEW_TAG, name.getPackageName());
            UiUtil.setItemValue(viewGroup, CLASS_VIEW_TAG, name.getClassName());
            mIdlingResource.setIdleState(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindService(this);//避免Service重新后又onServiceConnected

            UiUtil.setItemValue(viewGroup, STATUS_VIEW_TAG, "onServiceDisconnected");
            UiUtil.setItemValue(viewGroup, PACKAGE_VIEW_TAG, name.getPackageName());
            UiUtil.setItemValue(viewGroup, CLASS_VIEW_TAG, name.getClassName());
            mIdlingResource.setIdleState(true);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewGroup = UiUtil.setActivityContentView(this);

        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "ServiceConnection Callback",
                        STATUS_VIEW_TAG,
                        ""
                )
        );

        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "ComponentName.getPackageName()",
                        PACKAGE_VIEW_TAG,
                        ""
                )
        );

        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "ComponentName.getClassName()",
                        CLASS_VIEW_TAG,
                        ""
                )
        );

        Button bindService = new Button(this);
        bindService.setText("bindService");
        bindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                bindService();
                mIdlingResource.setIdleState(false);
            }
        });

        Button stopService = new Button(this);
        stopService.setText("stopService");
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                stopService();
                mIdlingResource.setIdleState(false);
            }
        });

        viewGroup.addView(bindService);
        viewGroup.addView(stopService);
    }

    private void bindService() {
        Intent intent = new Intent(this, SystemExitService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void stopService() {
        try {
            //随便发什么过去都表示杀进程
            service.transact(0, Parcel.obtain(), Parcel.obtain(), 0);
        } catch (RemoteException ignored) {
        }
        mIdlingResource.setIdleState(false);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
