package com.tencent.shadow.sample.api.hello;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
    public SampleHelloWorld(Context context) {

    }

    @Override
    public void sayHelloWorld(Context context, TextView textView) {
        TestDialog.sContetx = context;
        String text = "这是apk中的实现：" + SampleHelloWorld.class.toString();
        if (textView == null) {
            return;
        }
        textView.setText(text);

        new TestDialog(context).show();
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
