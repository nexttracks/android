package org.nexttracks.android.injection.modules.android.ServiceModules;

import android.app.Service;
import android.content.Context;

import org.nexttracks.android.injection.qualifier.ServiceContext;
import org.nexttracks.android.injection.scopes.PerService;

import dagger.Module;
import dagger.Provides;

@Module
abstract class BaseServiceModule {

    @Provides
    @PerService
    @ServiceContext
    static Context serviceContext(Service service) {
        return service;
    }

}
