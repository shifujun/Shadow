package com.tencent.shadow.core.runtime;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Intent;

@SuppressLint("NewApi")
public class ShadowJobScheduler {
    private final ShadowContext context;

    public ShadowJobScheduler(ShadowContext shadowContext) {
        this.context = shadowContext;
    }

    public int enqueue(JobInfo job, JobWorkItem work) {
        ComponentName service = job.getService();
        Intent intent = new Intent();
        intent.setComponent(service);
        context.startService(intent);
        ShadowJobServiceEngine engine = ShadowJobServiceEngine.sInstanceMap.get(service);
        if (engine != null) {
            engine.onStartJob(new ShadowJobParameters(context, job, work));
            return JobScheduler.RESULT_SUCCESS;
        } else {
            return JobScheduler.RESULT_FAILURE;
        }
    }
}
