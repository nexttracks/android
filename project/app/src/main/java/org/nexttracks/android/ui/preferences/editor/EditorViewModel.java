package org.nexttracks.android.ui.preferences.editor;

import androidx.databinding.Bindable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nexttracks.android.BR;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.messages.MessageConfiguration;
import org.nexttracks.android.support.Parser;
import org.nexttracks.android.support.Preferences;
import org.nexttracks.android.ui.base.viewmodel.BaseViewModel;

import java.io.IOException;

import javax.inject.Inject;


@PerActivity
public class EditorViewModel extends BaseViewModel<EditorMvvm.View> implements EditorMvvm.ViewModel<EditorMvvm.View> {
    private final Parser parser;
    private final Preferences preferences;
    @Bindable
    private String effectiveConfiguration;
    
    @Inject
    public EditorViewModel(Preferences preferences, Parser parser) {
        this.preferences = preferences;
        this.parser = parser; 
    }

    public void attachView(@NonNull EditorMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
        updateEffectiveConfiguration();
    }

    private void updateEffectiveConfiguration() {
        try {
            MessageConfiguration m = preferences.exportToMessage();
            m.set(Preferences.Keys.PASSWORD, "********");
            setEffectiveConfiguration(parser.toJsonPlainPretty(m));
        } catch (IOException e) {
            getView().displayLoadFailed();
        }
    }

    @Bindable
    public String getEffectiveConfiguration() {
        return effectiveConfiguration;
    }

    @Bindable
    private void setEffectiveConfiguration(String effectiveConfiguration) {
        this.effectiveConfiguration = effectiveConfiguration;
        notifyPropertyChanged(BR.effectiveConfiguration);
    }

    @Override
    public void onPreferencesValueForKeySetSuccessful() {
        updateEffectiveConfiguration();
        notifyPropertyChanged(BR.effectiveConfiguration);
    }
}
