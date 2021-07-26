package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.WithIdlingResourceActivity;

public class TestJobIntentServiceActivity extends WithIdlingResourceActivity {

    static Runnable sOnJobFinished;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ViewGroup viewGroup = UiUtil.setActivityContentView(this);

        Intent work = new Intent();
        work.putExtra("label", "TestJobIntentServiceActivity");
        TestJobIntentService.enqueueWork(this, work);

        sOnJobFinished = new Runnable() {
            @Override
            public void run() {
                TextView textView = new TextView(viewGroup.getContext());
                textView.setTag("textview");
                textView.setText("job finished");
                viewGroup.addView(textView);
                mIdlingResource.setIdleState(true);
            }
        };

        mIdlingResource.setIdleState(false);
    }

}
