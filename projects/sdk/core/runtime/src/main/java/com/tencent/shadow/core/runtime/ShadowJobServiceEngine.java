package com.tencent.shadow.core.runtime;

import android.content.ComponentName;

import java.util.HashMap;
import java.util.Map;

public abstract class ShadowJobServiceEngine {

    static Map<ComponentName, ShadowJobServiceEngine> sInstanceMap = new HashMap<>();

    public ShadowJobServiceEngine(ShadowService service) {
        sInstanceMap.put(new ComponentName(service, service.getClass()), this);
    }

    public abstract boolean onStartJob(ShadowJobParameters params);

    public abstract boolean onStopJob(ShadowJobParameters params);

}
