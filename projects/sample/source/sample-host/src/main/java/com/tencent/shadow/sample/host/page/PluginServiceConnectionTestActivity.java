package com.tencent.shadow.sample.host.page;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.sample.constant.Constant;
import com.tencent.shadow.sample.host.HostApplication;
import com.tencent.shadow.sample.host.PluginHelper;
import com.tencent.shadow.sample.host.PluginLoadActivity;
import com.tencent.shadow.sample.host.UiUtil;


/**
 * ServiceConnection回调测试
 * <p>
 * 点bindService按钮，预期收到onServiceConnected调用，显示出回调信息。
 * 再点stopService按钮，预期SystemExitService自己杀进程，然后收到onServiceDisconnected调用，显示出回调信息。
 */
public class PluginServiceConnectionTestActivity extends Activity {

    private static final String STATUS_VIEW_TAG = "STATUS_VIEW_TAG";
    private static final String PACKAGE_VIEW_TAG = "PACKAGE_VIEW_TAG";
    private static final String CLASS_VIEW_TAG = "CLASS_VIEW_TAG";

    private ViewGroup viewGroup;
    private IBinder service;

    final private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PluginServiceConnectionTestActivity.this.service = service;
            UiUtil.setItemValue(viewGroup, STATUS_VIEW_TAG, "onServiceConnected");
            UiUtil.setItemValue(viewGroup, PACKAGE_VIEW_TAG, name.getPackageName());
            UiUtil.setItemValue(viewGroup, CLASS_VIEW_TAG, name.getClassName());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindService(this);//避免Service重新后又onServiceConnected

            UiUtil.setItemValue(viewGroup, STATUS_VIEW_TAG, "onServiceDisconnected");
            UiUtil.setItemValue(viewGroup, PACKAGE_VIEW_TAG, name.getPackageName());
            UiUtil.setItemValue(viewGroup, CLASS_VIEW_TAG, name.getClassName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            }
        });

        Button stopService = new Button(this);
        stopService.setText("stopService");
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                stopService();
            }
        });

        viewGroup.addView(bindService);
        viewGroup.addView(stopService);
    }

    private void bindService() {
        PluginHelper.getInstance().singlePool.execute(new Runnable() {
            @Override
            public void run() {
                HostApplication.getApp().loadPluginManager(PluginHelper.getInstance().pluginManagerFile);

                Bundle bundle = new Bundle();
                bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, PluginHelper.getInstance().pluginZipFile.getAbsolutePath());
                bundle.putString(Constant.KEY_PLUGIN_PART_KEY, getIntent().getStringExtra(Constant.KEY_PLUGIN_PART_KEY));
                bundle.putString(Constant.KEY_ACTIVITY_CLASSNAME, getIntent().getStringExtra(Constant.KEY_ACTIVITY_CLASSNAME));

                HostApplication.getApp().getPluginManager()
                        .enter(PluginLoadActivity.this, Constant.FROM_ID_START_ACTIVITY, bundle, new EnterCallback() {
                            @Override
                            public void onShowLoadingView(final View view) {
                            }

                            @Override
                            public void onCloseLoadingView() {
                                finish();
                            }

                            @Override
                            public void onEnterComplete() {

                            }
                        });
            }
        });

        Intent intent = new Intent(this, SystemExitService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void stopService() {
        try {
            //随便发什么过去都表示杀进程
            service.transact(0, Parcel.obtain(), Parcel.obtain(), 0);
        } catch (RemoteException ignored) {
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
