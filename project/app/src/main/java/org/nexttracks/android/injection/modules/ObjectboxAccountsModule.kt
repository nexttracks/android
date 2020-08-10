package org.nexttracks.android.injection.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import org.nexttracks.android.data.repos.ObjectboxAccountsRepo
import org.nexttracks.android.data.repos.AccountsRepo
import org.nexttracks.android.injection.qualifier.AppContext
import org.nexttracks.android.injection.scopes.PerApplication
import org.nexttracks.android.support.Preferences

@Module
class ObjectboxAccountsModule {
    @Provides
    @PerApplication
    fun provideAccountsRepo(@AppContext context: Context, eventBus: EventBus, preferences: Preferences): AccountsRepo {
        return ObjectboxAccountsRepo(context, eventBus, preferences)
    }
}
