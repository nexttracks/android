package org.nexttracks.android.ui.preferences;

import org.nexttracks.android.support.Preferences;
import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;

public interface PreferencesFragmentMvvm {

    interface View extends MvvmView {
        void loadRoot();

        void setModeSummary(int mode);
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        Preferences getPreferences();
    }
}
