package com.tencent.cloudgamepluginbaseactivity;

public class PluginBinding {
    public static PluginMutableLiveDataFactory sPluginMutableLiveDataFactory;

    public interface PluginMutableLiveDataFactory {
        <T> PluginMutableLiveData<T> build();
    }
}
