package com.tencent.shadow.test.manager_test;

import android.content.Context;

import com.tencent.shadow.core.manager.BasePluginManager;

public class TestCoreManager extends BasePluginManager {
    public TestCoreManager(Context context) {
        super(context);
    }

    @Override
    protected String getName() {
        return TestCoreManager.class.getSimpleName();
    }
}
