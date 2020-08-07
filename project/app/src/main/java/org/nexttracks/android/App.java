package org.nexttracks.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import org.nexttracks.android.injection.components.AppComponent;
import org.nexttracks.android.injection.components.AppComponentProvider;
import org.nexttracks.android.injection.components.DaggerAppComponent;
import org.nexttracks.android.injection.qualifier.AppContext;
import org.nexttracks.android.services.MessageProcessor;
import org.nexttracks.android.support.Parser;
import org.nexttracks.android.support.Preferences;
import org.nexttracks.android.support.RunThingsOnOtherThreads;
import org.nexttracks.android.support.TimberDebugLogTree;
import org.nexttracks.android.ui.map.MapActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import timber.log.Timber;

public class App extends DaggerApplication  {
    private static Application sApplication;

    @Inject
    Preferences preferences;

    @Inject
    RunThingsOnOtherThreads runThingsOnOtherThreads;

    @Inject
    MessageProcessor messageProcessor;

    @Inject
    Parser parser;

    @Inject
    @AppContext
    Context context;

    @Override
    public void onCreate() {
        sApplication = this;
        WorkManager.initialize(this, new Configuration.Builder().build());

        super.onCreate();

        if(BuildConfig.DEBUG) {
            Timber.plant(new TimberDebugLogTree());
            Timber.e("StrictMode enabled in DEBUG build");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectNetwork()
                    .penaltyFlashScreen()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectFileUriExposure()
                    .penaltyLog()
                    .build());

        } else {
            Timber.plant(new Timber.DebugTree());
        }
        for(Timber.Tree t : Timber.forest()) {
                Timber.v("Planted trees :%s", t);
        }

        preferences.checkFirstStart();

        // Running this on a background thread will deadlock FirebaseJobDispatcher.
        // Initialize will call Scheduler to connect off the main thread anyway.
        runThingsOnOtherThreads.postOnMainHandlerDelayed(() -> messageProcessor.initialize(), 510);

    }


    public static void restart() {
        Intent intent = new Intent(App.getApplication().getApplicationContext(), MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getApplication().getApplicationContext().startActivity(intent);
        Runtime.getRuntime().exit(0);
    }

    @Override
    @NonNull
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().app(this).build();
        appComponent.inject(this);
        AppComponentProvider.setAppComponent(appComponent);
        return appComponent;
    }

    private static Application getApplication() {
        return sApplication;
    }

    @Deprecated
    @NonNull
    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

}
