package com.tencent.shadow.core.runtime;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Intent;

@SuppressLint("NewApi")
public class ShadowJobParameters {
    private final JobInfo job;
    private final ShadowContext context;
    private JobWorkItem work;

    public ShadowJobParameters(ShadowContext context, JobInfo job, JobWorkItem work) {
        this.job = job;
        this.context = context;
        this.work = work;
    }

    public JobWorkItem dequeueWork() {
        JobWorkItem work = this.work;
        this.work = null;
        return work;
    }

    public void completeWork(JobWorkItem work) {
        ComponentName service = job.getService();
        Intent intent = new Intent();
        intent.setComponent(service);
        context.stopService(intent);
    }
}
