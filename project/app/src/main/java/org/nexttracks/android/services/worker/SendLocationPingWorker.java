package org.nexttracks.android.services.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.nexttracks.android.injection.components.AppComponentProvider;
import org.nexttracks.android.messages.MessageLocation;
import org.nexttracks.android.services.LocationProcessor;

import javax.inject.Inject;

import timber.log.Timber;

public class SendLocationPingWorker extends Worker {
    @Inject
    LocationProcessor locationProcessor;

    public SendLocationPingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppComponentProvider.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        Timber.v("SendLocationPingWorker doing work. ThreadID: %s", Thread.currentThread());
        locationProcessor.publishLocationMessage(MessageLocation.REPORT_TYPE_PING);
        return Result.success();
    }
}