package com.tencent.shadow.test.dynamic.host;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.tencent.shadow.dynamic.host.DynamicPluginManager;
import com.tencent.shadow.dynamic.host.PluginManagerUpdater;
import com.tencent.shadow.test.lib.constant.Constant;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UpdateManagerImplTestActivity extends Activity {

    private DynamicPluginManager dynamicPluginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PluginManagerUpdater pluginManagerUpdater = new DumbUpdater(
                PluginHelper.getInstance().dumbManagerFile,
                PluginHelper.getInstance().pluginManagerFile
        );

        dynamicPluginManager = new DynamicPluginManager(pluginManagerUpdater);
        dynamicPluginManager.enter(this, Constant.FROM_ID_NOOP, null, null);
        String firstMangerImpl = dynamicPluginManager.getManagerImpl().getClass().getName();
        dynamicPluginManager.enter(this, Constant.FROM_ID_NOOP, null, null);
        String secondMangerImpl = dynamicPluginManager.getManagerImpl().getClass().getName();

        TextView textView = new TextView(this);
        textView.setText(firstMangerImpl + "/" + secondMangerImpl);
        textView.setTag("ImplName");
        setContentView(textView);
    }

    @Override
    protected void onDestroy() {
        dynamicPluginManager.release();
        super.onDestroy();
    }
}

class DumbUpdater implements PluginManagerUpdater {

    final private File dumbManagerApk;
    final private File workedManagerApk;

    DumbUpdater(File dumbManagerApk, File workedManagerApk) {
        this.dumbManagerApk = dumbManagerApk;
        this.workedManagerApk = workedManagerApk;
    }


    @Override
    public boolean wasUpdating() {
        return false;
    }

    @Override
    public Future<File> update() {
        try {
            FileUtils.copyInputStreamToFile(new FileInputStream(workedManagerApk), dumbManagerApk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File getLatest() {
        return dumbManagerApk;
    }

    @Override
    public Future<Boolean> isAvailable(final File file) {
        return null;
    }
}