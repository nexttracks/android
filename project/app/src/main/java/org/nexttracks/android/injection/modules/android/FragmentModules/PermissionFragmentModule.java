package org.nexttracks.android.injection.modules.android.FragmentModules;


import androidx.fragment.app.Fragment;

import org.nexttracks.android.ui.welcome.permission.PermissionFragment;
import org.nexttracks.android.ui.welcome.permission.PermissionFragmentMvvm;
import org.nexttracks.android.ui.welcome.permission.PermissionFragmentViewModel;

import dagger.Binds;
import dagger.Module;

@Module(includes = BaseSupportFragmentModule.class)
public abstract class PermissionFragmentModule {

    @Binds
    abstract Fragment bindSupportFragment(PermissionFragment f);

    @Binds abstract PermissionFragmentMvvm.ViewModel bindViewModel(PermissionFragmentViewModel viewModel);
}