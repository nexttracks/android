package org.nexttracks.android.injection.modules.android.ServiceModules

import android.app.Service
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import org.nexttracks.android.injection.qualifier.ServiceContext
import org.nexttracks.android.injection.scopes.PerActivity
import org.nexttracks.android.injection.scopes.PerService
import org.nexttracks.android.services.BackgroundService

@Module
abstract class BackgroundServiceModule {
    @Binds
    @PerActivity
    abstract fun bindService(s: BackgroundService?): Service?

    companion object {
        @JvmStatic
        @Provides
        @PerService
        @ServiceContext
        fun serviceContext(service: Service): Context {
            return service
        }
    }
}