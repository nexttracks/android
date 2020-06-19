package org.nexttracks.android.ui.welcome.version;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import org.nexttracks.android.R;
import org.nexttracks.android.databinding.UiWelcomeVersionBinding;
import org.nexttracks.android.ui.base.BaseSupportFragment;
import org.nexttracks.android.ui.base.viewmodel.NoOpViewModel;
import org.nexttracks.android.ui.welcome.WelcomeFragmentMvvm;

public class VersionFragment extends BaseSupportFragment<UiWelcomeVersionBinding, NoOpViewModel> implements WelcomeFragmentMvvm.View, View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = setAndBindContentView(inflater, container, R.layout.ui_welcome_version, savedInstanceState);
        binding.uiFragmentWelcomeVersionButtonLearnMore.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.valDocumentationUrlAndroid)));
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No suitable browser installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isNextEnabled() {
        return true;
    }

    @Override
    public void onShowFragment() {

    }
}