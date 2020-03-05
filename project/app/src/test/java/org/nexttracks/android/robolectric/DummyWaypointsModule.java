package org.nexttracks.android.robolectric;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.nexttracks.android.data.repos.WaypointsRepo;
import org.nexttracks.android.injection.qualifier.AppContext;
import org.nexttracks.android.injection.scopes.PerApplication;
import org.nexttracks.android.support.Preferences;

import dagger.Module;
import dagger.Provides;

@Module
public class DummyWaypointsModule{
    @PerApplication
    @Provides
    WaypointsRepo provideWaypointsRepo(@AppContext Context context, EventBus eventBus, Preferences preferences) {
        return new DummyWaypointsRepo(eventBus);
    }

}
