package com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.TestApplication;

public class FragmentStartedActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = new Button(this);
        button.setText("finish");
        button.setTag("finish_button");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentStartedActivity.this.finish();
            }
        });

        setContentView(button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TestApplication.getInstance().decrementCountingIdlingResource();
    }
}
