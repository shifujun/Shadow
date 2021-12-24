package com.tencent.shadow.sample.plugin.app.lib.usecases.activity.race;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * @author huangxiaohui
 * @date 2021/12/23
 */
public class MqttDataResolver {
    private ArrayList<OnMqttMessageComeListener> messageComeListeners = new ArrayList<OnMqttMessageComeListener>();
    private MsgHandler msgHandler = new MsgHandler();

    public static MqttDataResolver getInstance() {
        return Inner.instance;
    }

    private MqttDataResolver() {
        startTestThread();
    }

    private void startTestThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ArrayList<RaceTrackItemData> tracks = new ArrayList<>();
                int scale = 10;
                for (int i = 0; i <= 360 * scale; i++) {
                    double angle = (i / (1.0 * 360 * scale)) * 2 * Math.PI;
                    double x = Math.cos(angle) * 0.01 + 39.983186;
                    double y = Math.sin(angle) * 0.01 + 116.306503;
                    tracks.add(new RaceTrackItemData(x, y));
                }

                ArrayList<UserItemData> users = new ArrayList<UserItemData>();
                String icon = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2F2021%2Fedpic_360_360%2F96%2F1d%2Fe9%2F961de9fd3aae1656df8234fad2c004f6_1.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642683084&t=ddbdb1836aadf964c4337cd037bcccb5";
                for (int i = 1; i <= 6; i++) {
                    double x = Math.cos(0) * 0.01 + 39.983186;
                    double y = Math.sin(0) * 0.01 + 116.306503;
                    users.add(new UserItemData("" + i, String.format("黄%d%d", i, i), x, y, icon, 0));
                }

                int[] steps = new int[users.size()];
                int[] poses = new int[users.size()];
                for (int i = 0; i < steps.length; i++) {
                    steps[i] = i + 1;
                }
                while (true) {
                    for (int i = 0; i < users.size(); i++) {
                        UserItemData user = users.get(i);
                        poses[i] = poses[i] + steps[i];
                        RaceTrackItemData track = tracks.get(poses[i] % tracks.size());
                        user.lat = track.lat;
                        user.lng = track.lng;
                        user.distance = poses[i];
                        msgHandler.sendUserCome(user);
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    private class MsgHandler extends Handler {
        public static final int WHAT_USER_COME = 0x0000;

        public void sendUserCome(UserItemData user) {
            Message msg = obtainMessage(WHAT_USER_COME);
            msg.obj = user;
            sendMessage(msg);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == WHAT_USER_COME) {
                UserItemData user = (UserItemData) msg.obj;
                for (OnMqttMessageComeListener messageComeListener : messageComeListeners) {
                    messageComeListener.onNewUserCome(user);
                }
            }
        }
    }

    public void addMessageComeListener(OnMqttMessageComeListener listener) {
        if(!messageComeListeners.contains(listener)) {
            messageComeListeners.add(listener);
        }
    }

    public void removeMessageComeListener(OnMqttMessageComeListener listener) {
        if(messageComeListeners.contains(listener)) {
            messageComeListeners.remove(listener);
        }
    }

    private static class Inner {
        private static MqttDataResolver instance = new MqttDataResolver();
    }

}
