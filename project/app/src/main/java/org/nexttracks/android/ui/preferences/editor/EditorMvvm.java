package org.nexttracks.android.ui.preferences.editor;
import androidx.databinding.Bindable;

import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;
public interface EditorMvvm {

    interface View extends MvvmView {
        void displayLoadFailed();

        void displayExportToFileFailed();
        void displayExportToFileSuccessful();

        boolean exportConfigurationToFile(String exportStr);
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        void onPreferencesValueForKeySetSuccessful();
        @Bindable String getEffectiveConfiguration();
    }
}
