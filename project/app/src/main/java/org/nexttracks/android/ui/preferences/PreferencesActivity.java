package org.nexttracks.android.ui.preferences;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.nexttracks.android.R;
import org.nexttracks.android.databinding.UiPreferencesBinding;
import org.nexttracks.android.ui.base.BaseActivity;
import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.NoOpViewModel;

public class PreferencesActivity extends BaseActivity<UiPreferencesBinding, NoOpViewModel> implements MvvmView, PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_preferences);
        bindAndAttachContentView(R.layout.ui_preferences, savedInstanceState);
        setHasEventBus(false);
        setSupportToolbar(this.binding.toolbar, true, true);
        setDrawer(binding.toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
                    if (getSupportFragmentManager().getFragments().isEmpty()) {
                        setToolbarTitle(getTitle());
                    } else {

                        setToolbarTitle(((PreferenceFragmentCompat) getSupportFragmentManager().getFragments().get(0)).getPreferenceScreen().getTitle());
                    }
                }
        );
        navigator.replaceFragment(R.id.content_frame, new PreferencesFragment(), null, this);
    }

    private void setToolbarTitle(CharSequence text) {
        binding.toolbar.setTitle(text);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(pref.getKey())
                .commit();
        return true;
    }
}
