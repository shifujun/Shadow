package com.tencent.shadow.sample.plugin.app.lib.usecases.flutter;

import android.content.Context;
import android.content.pm.PackageManager;

import io.flutter.embedding.android.FlutterActivity;

public class TestFlutterActivity extends FlutterActivity {
    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return this;
    }

}
