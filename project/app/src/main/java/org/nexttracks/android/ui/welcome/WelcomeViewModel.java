package org.nexttracks.android.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nexttracks.android.App;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.support.Preferences;
import org.nexttracks.android.ui.base.viewmodel.BaseViewModel;
import org.nexttracks.android.ui.map.MapActivity;

import javax.inject.Inject;

import timber.log.Timber;


@PerActivity
public class WelcomeViewModel extends BaseViewModel<WelcomeMvvm.View> implements WelcomeMvvm.ViewModel<WelcomeMvvm.View> {

    private final Preferences preferences;
    private boolean doneEnabled;
    private boolean nextEnabled;

    @Inject
    public WelcomeViewModel(Preferences preferences) {
        this.preferences = preferences;
    }
    public void attachView(@NonNull WelcomeMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
    }

    @Override
    public void onAdapterPageSelected(int position) {
        getView().setPagerIndicator(position);
    }

    @Override
    public void onNextClicked() {
        Timber.v("onNextClicked next:%s, done:%s", nextEnabled, doneEnabled);
        getView().showNextFragment();

    }

    @Override
    public void onDoneClicked() {
        Timber.v("onDoneClicked next:%s, done:%s", nextEnabled, doneEnabled);
        preferences.setSetupCompleted();
        navigator.startActivity(MapActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void setNextEnabled(boolean enabled) {
        this.nextEnabled = enabled;
        notifyChange();
    }

    @Override
    public boolean isNextEnabled() {
        return nextEnabled;
    }

    @Override
    public boolean isDoneEnabled() {
        return doneEnabled;
    }

    @Override
    public void setDoneEnabled(boolean enabled) {
        this.doneEnabled = enabled;
        notifyChange();
    }

}
