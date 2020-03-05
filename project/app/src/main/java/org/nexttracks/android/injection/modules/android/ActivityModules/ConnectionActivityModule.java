package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.appcompat.app.AppCompatActivity;

import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.ui.preferences.connection.ConnectionActivity;
import org.nexttracks.android.ui.preferences.connection.ConnectionMvvm;
import org.nexttracks.android.ui.preferences.connection.ConnectionViewModel;

import dagger.Binds;
import dagger.Module;

@Module(includes = BaseActivityModule.class)
public abstract class ConnectionActivityModule {

    @Binds
    @PerActivity
    abstract AppCompatActivity bindActivity(ConnectionActivity a);

    @Binds abstract ConnectionMvvm.ViewModel bindViewModel(ConnectionViewModel viewModel);
}