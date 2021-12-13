package com.tencent.cloudgamepluginbaseactivity.component;

import com.tencent.cloudgamepluginbaseactivity.SplashViewModel;

public class ParameterContext {
    private static ParameterContext sInstance;

    final private SplashViewModel _splashViewModel;

    public static void init(SplashViewModel m) {
        Class c = ParameterContext.class;
        sInstance = new ParameterContext(m);
    }

    private ParameterContext(SplashViewModel m) {
        this._splashViewModel = m;
    }

    public static ParameterContext getInstance() {
        Class c = ParameterContext.class;
        return sInstance;
    }

    public SplashViewModel get_splashViewModel() {
        return _splashViewModel;
    }
}
