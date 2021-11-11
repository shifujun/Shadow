package com.tencent.shadow;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.tencent.shadow.dialog.TestDialog;

public class HelloApkActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = new Button(this);
        button.setText("show Dialog");
        button.setOnClickListener(v -> {
            button.setEnabled(false);

            new TestDialog(HelloApkActivity.this).show();

        });
        setContentView(button);
    }
}
