package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.appcompat.app.AppCompatActivity;

import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.ui.map.MapActivity;
import org.nexttracks.android.ui.map.MapMvvm;
import org.nexttracks.android.ui.map.MapViewModel;

import dagger.Binds;
import dagger.Module;

@Module(includes = BaseActivityModule.class)
public abstract class MapActivityModule {

    @Binds
    @PerActivity
    abstract AppCompatActivity bindActivity(MapActivity a);

    @Binds abstract MapMvvm.ViewModel bindViewModel(MapViewModel viewModel);
}