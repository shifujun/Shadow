package com.tencent.shadow.sample.api.hello;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.TextView;

import com.tencent.shadow.dialog.TestDialog;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/6
 * @description 实现宿主自定义接口
 * @usage null
 */
public class SampleHelloWorld implements IHelloWorldImpl {
    private final Context apkContext;
    public SampleHelloWorld(Context context) {
        apkContext = context;
    }

    @Override
    public void sayHelloWorld(Context activityContext, TextView textView) {
        String text = "这是apk中的实现：" + SampleHelloWorld.class.toString();
        if (textView == null) {
            return;
        }
        textView.setText(text);

        ContextThemeWrapper mixContext = new ContextThemeWrapper(apkContext, android.R.style.Theme_DeviceDefault) {
            @Override
            public Object getSystemService(String name) {
                if (WINDOW_SERVICE.equals(name)) {
                    return activityContext.getSystemService(name);
                } else {
                    return super.getSystemService(name);
                }
            }
        };
        new TestDialog(mixContext).show();
    }

    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroy() {

    }
}
