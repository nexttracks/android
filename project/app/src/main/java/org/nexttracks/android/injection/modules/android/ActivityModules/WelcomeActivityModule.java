package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.appcompat.app.AppCompatActivity;


import org.nexttracks.android.injection.modules.android.FragmentModules.FinishFragmentModule;
import org.nexttracks.android.injection.modules.android.FragmentModules.IntroFragmentModule;
import org.nexttracks.android.injection.modules.android.FragmentModules.PermissionFragmentModule;
import org.nexttracks.android.injection.modules.android.FragmentModules.VersionFragmentModule;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.injection.scopes.PerFragment;
import org.nexttracks.android.ui.welcome.WelcomeActivity;
import org.nexttracks.android.ui.welcome.WelcomeMvvm;
import org.nexttracks.android.ui.welcome.WelcomeViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module(includes = BaseActivityModule.class)
public abstract class WelcomeActivityModule {

    @Binds
    @PerActivity
    abstract AppCompatActivity bindActivity(WelcomeActivity a);

    @Binds
    @PerActivity
    abstract WelcomeMvvm.ViewModel bindViewModel(WelcomeViewModel viewModel);


    @ContributesAndroidInjector(modules = {IntroFragmentModule.class})
    @PerFragment
    abstract org.nexttracks.android.ui.welcome.intro.IntroFragment bindIntroFragment();

    @ContributesAndroidInjector(modules = {VersionFragmentModule.class})
    @PerFragment
    abstract org.nexttracks.android.ui.welcome.version.VersionFragment bindVersionFragment();

    @ContributesAndroidInjector(modules = {PermissionFragmentModule.class})
    @PerFragment
    abstract org.nexttracks.android.ui.welcome.permission.PermissionFragment bindPermissionFragment();

    @ContributesAndroidInjector(modules = {FinishFragmentModule.class})
    @PerFragment
    abstract org.nexttracks.android.ui.welcome.finish.FinishFragment bindFinishFragment();
}