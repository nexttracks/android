package org.nexttracks.android.ui.welcome.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.nexttracks.android.R;
import org.nexttracks.android.databinding.UiWelcomePermissionsBinding;
import org.nexttracks.android.support.Events;
import org.nexttracks.android.ui.base.BaseSupportFragment;
import org.nexttracks.android.ui.welcome.WelcomeMvvm;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PermissionFragment extends BaseSupportFragment<UiWelcomePermissionsBinding, PermissionFragmentMvvm.ViewModel> implements PermissionFragmentMvvm.View {
    private final int PERMISSIONS_REQUEST_CODE = 1;

    @Inject
    EventBus eventBus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return setAndBindContentView(inflater, container, R.layout.ui_welcome_permissions, savedInstanceState);
    }

    public void requestFix() {
        List<String> permissions = new ArrayList<String>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requestPermissions(permissions.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            eventBus.postSticky(new Events.PermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION));
        }
        checkPermission();
    }

    public void checkPermission() {
        viewModel.setPermissionGranted(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        ((WelcomeMvvm.View) getActivity()).setNextEnabled(viewModel.isPermissionGranted());
    }

    @Override
    public void onNextClicked() {

    }

    @Override
    public boolean isNextEnabled() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onShowFragment() {
        checkPermission();
    }
}
