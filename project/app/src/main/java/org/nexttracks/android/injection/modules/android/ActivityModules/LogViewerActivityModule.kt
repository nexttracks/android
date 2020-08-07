package org.nexttracks.android.injection.modules.android.ActivityModules

import androidx.appcompat.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import org.nexttracks.android.injection.scopes.PerActivity
import org.nexttracks.android.ui.preferences.LogViewerActivity

@Module(includes = [BaseActivityModule::class])
abstract class LogViewerActivityModule {
    @Binds
    @PerActivity
    abstract fun bindActivity(a: LogViewerActivity): AppCompatActivity
}