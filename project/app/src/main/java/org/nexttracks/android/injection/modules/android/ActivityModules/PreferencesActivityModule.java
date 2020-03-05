package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.appcompat.app.AppCompatActivity;

import org.nexttracks.android.injection.modules.android.FragmentModules.PreferencesFragmentModule;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.injection.scopes.PerFragment;
import org.nexttracks.android.ui.preferences.PreferencesActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module(includes = BaseActivityModule.class)
public abstract class PreferencesActivityModule {

    @Binds
    @PerActivity
    abstract AppCompatActivity bindActivity(PreferencesActivity a);

    @ContributesAndroidInjector(modules = {PreferencesFragmentModule.class})
    @PerFragment
    abstract org.nexttracks.android.ui.preferences.PreferencesFragment bindPreferencesFragment();

}