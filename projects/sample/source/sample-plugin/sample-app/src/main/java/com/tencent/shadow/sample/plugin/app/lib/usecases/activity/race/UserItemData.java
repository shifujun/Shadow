package com.tencent.shadow.sample.plugin.app.lib.usecases.activity.race;

/**
 * @author huangxiaohui
 * @date 2021/12/23
 */
public class UserItemData {
    public String id;
    public String name;
    public double lat;
    public double lng;
    public String icon;
    public int distance;

    public UserItemData(String id, String name, double lat, double lng, String icon, int distance) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.icon = icon;
    }
}
