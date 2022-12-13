package com.tencent.shadow.test.plugin.general_cases.lib.usecases.resources;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TestDeclareStyleableActivity extends Activity {
    private ViewGroup mItemViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemViewGroup = UiUtil.setActivityContentView(this);

        TestAttrView testAttrView = (TestAttrView) getLayoutInflater().inflate(R.layout.layout_test_attrs, null);


        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "refName",
                        "refName",
                        testAttrView.refName
                )
        );
    }
}
