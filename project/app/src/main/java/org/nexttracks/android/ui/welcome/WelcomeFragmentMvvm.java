package org.nexttracks.android.ui.welcome;

import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;

public interface WelcomeFragmentMvvm  {


    interface View extends MvvmView {
        void onNextClicked();
        boolean isNextEnabled();

        void onShowFragment();
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
    }
}
