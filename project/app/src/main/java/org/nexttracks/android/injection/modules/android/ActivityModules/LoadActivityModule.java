package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.appcompat.app.AppCompatActivity;

import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.ui.preferences.load.LoadActivity;
import org.nexttracks.android.ui.preferences.load.LoadMvvm;
import org.nexttracks.android.ui.preferences.load.LoadViewModel;

import dagger.Binds;
import dagger.Module;


@Module(includes = BaseActivityModule.class)
public abstract class LoadActivityModule {

    @Binds
    @PerActivity
    abstract AppCompatActivity bindActivity(LoadActivity a);

    @Binds abstract LoadMvvm.ViewModel bindViewModel(LoadViewModel viewModel);
}
