package org.nexttracks.android.ui.welcome.permission;

import androidx.databinding.Bindable;

import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.welcome.WelcomeFragmentMvvm;


public interface PermissionFragmentMvvm {
    interface View extends WelcomeFragmentMvvm.View {
        void requestFix();
    }

    interface ViewModel<V extends MvvmView> extends WelcomeFragmentMvvm.ViewModel<V> {
        void onFixClicked();

        @Bindable
        boolean isPermissionGranted();

        void setPermissionGranted(boolean permissionGranted);
    }
}
