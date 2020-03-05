package org.nexttracks.android.injection.modules.android.FragmentModules;


import androidx.fragment.app.Fragment;

import org.nexttracks.android.injection.scopes.PerFragment;
import org.nexttracks.android.ui.welcome.finish.FinishFragment;

import dagger.Binds;
import dagger.Module;

@Module(includes = BaseSupportFragmentModule.class)
public abstract class FinishFragmentModule {

    @Binds
    @PerFragment
    abstract Fragment bindSupportFragment(FinishFragment f);
}