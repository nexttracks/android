package org.nexttracks.android.ui.preferences;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import org.nexttracks.android.R;
import org.nexttracks.android.databinding.UiPreferencesBinding;
import org.nexttracks.android.ui.base.BaseActivity;
import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.NoOpViewModel;

import timber.log.Timber;

public class PreferencesActivity extends BaseActivity<UiPreferencesBinding, NoOpViewModel> implements MvvmView{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindAndAttachContentView(R.layout.ui_preferences, savedInstanceState);

        setHasEventBus(false);
        setSupportToolbar(this.binding.toolbar, true, true);
        setDrawer(binding.toolbar);

        navigator.replaceFragment(R.id.content_frame, new PreferencesFragment(), null );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);

        //Reloaded all preferences the mode is changed
        if(requestCode == PreferencesFragment.REQUEST_CODE_CONNECTION) {
            Timber.v("onActivityResult with REQUEST_CODE_CONNECTION");
            navigator.replaceFragment(R.id.content_frame, new PreferencesFragment(), null );
        }
    }
}
