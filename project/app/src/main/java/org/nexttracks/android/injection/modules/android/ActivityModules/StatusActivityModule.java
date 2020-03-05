package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.appcompat.app.AppCompatActivity;

import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.ui.status.StatusActivity;
import org.nexttracks.android.ui.status.StatusMvvm;
import org.nexttracks.android.ui.status.StatusViewModel;

import dagger.Binds;
import dagger.Module;


@Module(includes = BaseActivityModule.class)
public abstract class StatusActivityModule {

    @Binds
    @PerActivity
    abstract AppCompatActivity bindActivity(StatusActivity a);

    @Binds abstract StatusMvvm.ViewModel bindViewModel(StatusViewModel viewModel);
}