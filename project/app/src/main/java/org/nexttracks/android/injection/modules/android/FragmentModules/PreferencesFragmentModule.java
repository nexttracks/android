package org.nexttracks.android.injection.modules.android.FragmentModules;

import androidx.fragment.app.Fragment;

import org.nexttracks.android.injection.scopes.PerFragment;
import org.nexttracks.android.ui.preferences.PreferencesFragment;
import org.nexttracks.android.ui.preferences.PreferencesFragmentMvvm;
import org.nexttracks.android.ui.preferences.PreferencesFragmentViewModel;

import dagger.Binds;
import dagger.Module;

@Module(includes = BaseFragmentModule.class)
public abstract class PreferencesFragmentModule {

    @Binds
    @PerFragment
    abstract Fragment bindFragment(PreferencesFragment f);

    @Binds abstract PreferencesFragmentMvvm.ViewModel bindViewModel(PreferencesFragmentViewModel viewModel);
}
