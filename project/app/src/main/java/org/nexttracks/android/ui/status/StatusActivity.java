package org.nexttracks.android.ui.status;

import android.os.Bundle;
import androidx.annotation.Nullable;

import org.nexttracks.android.R;
import org.nexttracks.android.databinding.UiStatusBinding;
import org.nexttracks.android.ui.base.BaseActivity;


public class StatusActivity extends BaseActivity<UiStatusBinding, StatusMvvm.ViewModel> implements StatusMvvm.View {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindAndAttachContentView(R.layout.ui_status, savedInstanceState);
        setSupportToolbar(binding.toolbar);
        setDrawer(binding.toolbar);
    }
}
