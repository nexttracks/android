package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.appcompat.app.AppCompatActivity;

import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.ui.regions.RegionsActivity;
import org.nexttracks.android.ui.regions.RegionsMvvm;
import org.nexttracks.android.ui.regions.RegionsViewModel;

import dagger.Binds;
import dagger.Module;

@Module(includes = BaseActivityModule.class)
public abstract class RegionsActivityModule {

    @Binds
    @PerActivity
    abstract AppCompatActivity bindActivity(RegionsActivity a);

    @Binds abstract RegionsMvvm.ViewModel bindViewModel(RegionsViewModel viewModel);
}