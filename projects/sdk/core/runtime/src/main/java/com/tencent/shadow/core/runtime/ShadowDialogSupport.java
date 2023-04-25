package com.tencent.shadow.core.runtime;

import android.app.Dialog;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

import tshadow.app.Activity;

public class ShadowDialogSupport {

    public static void dialogSetOwnerActivity(Dialog dialog, Activity activity) {
        android.app.Activity hostActivity = (android.app.Activity) activity.hostActivityDelegator.getHostActivity();
        dialog.setOwnerActivity(hostActivity);
    }

    public static Activity dialogGetOwnerActivity(Dialog dialog) {
        PluginContainerActivity ownerActivity = (PluginContainerActivity) dialog.getOwnerActivity();
        if (ownerActivity != null) {
            return (Activity) PluginActivity.get(ownerActivity);
        } else {
            return null;
        }
    }

}
