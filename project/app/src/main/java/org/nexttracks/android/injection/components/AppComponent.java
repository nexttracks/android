package org.nexttracks.android.injection.components;

import androidx.annotation.NonNull;

import org.nexttracks.android.App;
import org.nexttracks.android.injection.modules.AppModule;
import org.nexttracks.android.injection.modules.ObjectboxWaypointsModule;
import org.nexttracks.android.injection.modules.android.AndroindBindingModule;
import org.nexttracks.android.injection.scopes.PerApplication;
import org.nexttracks.android.services.worker.MQTTKeepaliveWorker;
import org.nexttracks.android.services.worker.MQTTReconnectWorker;
import org.nexttracks.android.services.worker.SendLocationPingWorker;
import org.nexttracks.android.support.preferences.SharedPreferencesStoreModule;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

@PerApplication
@Component(modules={
        AppModule.class,
        ObjectboxWaypointsModule.class,
        AndroidSupportInjectionModule.class,
        AndroindBindingModule.class,
        SharedPreferencesStoreModule.class}
        )
public interface AppComponent extends AndroidInjector<DaggerApplication>  {

    @Component.Builder
    interface Builder {
        @BindsInstance
        @NonNull
        Builder app(@NonNull App app);
        @NonNull
        AppComponent build();

    }

    @Override
    void inject(DaggerApplication instance);
    void inject(App app);
    void inject(MQTTKeepaliveWorker worker);
    void inject(MQTTReconnectWorker worker);
    void inject(SendLocationPingWorker worker);

}
