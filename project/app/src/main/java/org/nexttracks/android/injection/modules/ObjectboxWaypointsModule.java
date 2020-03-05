package org.nexttracks.android.injection.modules;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.nexttracks.android.data.repos.ObjectboxWaypointsRepo;
import org.nexttracks.android.data.repos.WaypointsRepo;
import org.nexttracks.android.injection.qualifier.AppContext;
import org.nexttracks.android.injection.scopes.PerApplication;
import org.nexttracks.android.support.Preferences;

import dagger.Module;
import dagger.Provides;

@Module
public class ObjectboxWaypointsModule {
    @Provides
    @PerApplication
    protected WaypointsRepo provideWaypointsRepo(@AppContext Context context, EventBus eventBus, Preferences preferences) {
        return new ObjectboxWaypointsRepo(context, eventBus, preferences);
    }
}
