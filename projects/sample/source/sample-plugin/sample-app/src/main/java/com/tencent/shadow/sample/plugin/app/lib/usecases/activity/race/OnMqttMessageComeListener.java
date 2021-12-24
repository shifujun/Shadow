package com.tencent.shadow.sample.plugin.app.lib.usecases.activity.race;

/**
 * @author huangxiaohui
 * @date 2021/12/23
 */
public interface OnMqttMessageComeListener {
    public void onNewUserCome(UserItemData user);
}
