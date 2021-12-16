package com.tencent.cloudgamepluginbaseactivity;

public interface PluginMutableLiveData<T> {
    void postValue(T value);

    void setValue(T value);
}
