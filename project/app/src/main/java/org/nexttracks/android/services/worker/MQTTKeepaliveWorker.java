package org.nexttracks.android.services.worker;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.nexttracks.android.injection.components.AppComponentProvider;
import org.nexttracks.android.services.MessageProcessor;

import javax.inject.Inject;

import timber.log.Timber;

public class MQTTKeepaliveWorker extends Worker {

    @Inject
    MessageProcessor messageProcessor;

    public MQTTKeepaliveWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppComponentProvider.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        Timber.tag("outgoing").v("MQTTKeepaliveWorker doing work. ThreadID: %s", Thread.currentThread());
        return messageProcessor.statefulSendKeepalive() ? Result.success() : Result.retry();
    }
}