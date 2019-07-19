package com.tencent.shadow.sample.plugin.app_2.lib;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.sample.host.lib.HostApplicationProxy;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Plugin_2", "Hello, I am Activity in Plugin 2. Process:"+Process.myPid());
        TextView callServiceButton = findViewById(R.id.button);
        callServiceButton.setText("调用插件Service，结果打印到Log");
        callServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HostApplicationProxy.getApp().callPluginService(MainActivity.this);
            }
        });
    }
}
