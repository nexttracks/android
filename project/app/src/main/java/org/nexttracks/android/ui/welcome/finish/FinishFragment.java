package org.nexttracks.android.ui.welcome.finish;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.nexttracks.android.R;
import org.nexttracks.android.databinding.UiWelcomeFinishBinding;
import org.nexttracks.android.ui.base.BaseSupportFragment;
import org.nexttracks.android.ui.base.viewmodel.NoOpViewModel;
import org.nexttracks.android.ui.welcome.WelcomeFragmentMvvm;


public class FinishFragment extends BaseSupportFragment<UiWelcomeFinishBinding, NoOpViewModel> implements WelcomeFragmentMvvm.View {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return setAndBindContentView(inflater, container, R.layout.ui_welcome_finish, savedInstanceState);
    }

    @Override
    public void onNextClicked() {
    }

    @Override
    public boolean isNextEnabled() {
        return false;
    }

    @Override
    public void onShowFragment() {

    }
}
