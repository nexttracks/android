package org.nexttracks.android.services.worker;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.nexttracks.android.injection.components.AppComponentProvider;
import org.nexttracks.android.services.MessageProcessor;

import javax.inject.Inject;

import timber.log.Timber;

public class MQTTMaybeReconnectAndPingWorker extends Worker {

    @Inject
    MessageProcessor messageProcessor;

    public MQTTMaybeReconnectAndPingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppComponentProvider.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        Timber.v("MQTTMaybeReconnectAndPingWorker doing work on threadID: %s", Thread.currentThread());
        if (!messageProcessor.isEndpointConfigurationComplete())
            return Result.failure();
        return messageProcessor.statefulReconnectAndSendKeepalive() ? Result.success() : Result.retry();
    }
}