package com.tencent.shadow.sample.host.lib;

import android.content.Context;

public interface DialogPlugin {
    class Impl {
        public static DialogPlugin impl = null;
    }

    void showDialog(Context context);
}
