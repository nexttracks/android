package org.nexttracks.android.injection.modules;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.nexttracks.android.App;
import org.nexttracks.android.data.repos.ContactsRepo;
import org.nexttracks.android.data.repos.LocationRepo;
import org.nexttracks.android.data.repos.MemoryContactsRepo;
import org.nexttracks.android.injection.qualifier.AppContext;
import org.nexttracks.android.injection.scopes.PerApplication;
import org.nexttracks.android.support.ContactImageProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @AppContext
    @PerApplication
    protected Context provideContext(App app) {
        return app;
    }

    @Provides
    @PerApplication
    protected EventBus provideEventbus() {
        return EventBus.builder().addIndex(new org.nexttracks.android.EventBusIndex()).sendNoSubscriberEvent(false).logNoSubscriberMessages(false).build();
    }


    @Provides
    @PerApplication
    protected ContactsRepo provideContactsRepo(EventBus eventBus, ContactImageProvider contactImageProvider) {
        return new MemoryContactsRepo(eventBus, contactImageProvider);
    }

    @Provides
    @PerApplication
    protected LocationRepo provideLocationRepo(EventBus eventBus) { return new LocationRepo(eventBus); }
}

