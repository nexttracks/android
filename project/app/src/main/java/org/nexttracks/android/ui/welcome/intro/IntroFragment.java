package org.nexttracks.android.ui.welcome.intro;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.nexttracks.android.R;
import org.nexttracks.android.databinding.UiWelcomeIntroBinding;
import org.nexttracks.android.ui.base.BaseSupportFragment;
import org.nexttracks.android.ui.base.viewmodel.NoOpViewModel;
import org.nexttracks.android.ui.welcome.WelcomeFragmentMvvm;

public class IntroFragment extends BaseSupportFragment<UiWelcomeIntroBinding, NoOpViewModel> implements WelcomeFragmentMvvm.View {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return setAndBindContentView(inflater, container, R.layout.ui_welcome_intro, savedInstanceState);
    }

    @Override
    public void onNextClicked() {
    }

    @Override
    public boolean isNextEnabled() {
        return true;
    }

    @Override
    public void onShowFragment() {

    }
}
