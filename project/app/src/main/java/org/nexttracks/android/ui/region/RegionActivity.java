package org.nexttracks.android.ui.region;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import org.nexttracks.android.R;
import org.nexttracks.android.databinding.UiRegionBinding;
import org.nexttracks.android.ui.base.BaseActivity;
import org.nexttracks.android.ui.map.MapActivity;

import androidx.annotation.Nullable;

import timber.log.Timber;

public class RegionActivity extends BaseActivity<UiRegionBinding, RegionMvvm.ViewModel> implements RegionMvvm.View {

    private MenuItem moveButton;
    private MenuItem saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasEventBus(false);
        bindAndAttachContentView(R.layout.ui_region, savedInstanceState);
        setSupportToolbar(binding.toolbar);

        Bundle b = navigator.getExtrasBundle(getIntent());
        if (b != null) {
            viewModel.loadWaypoint(b.getLong("waypointId", 0));
        }

        binding.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                conditionallyEnableSaveButton();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_waypoint, menu);
        this.moveButton = menu.findItem(R.id.move);
        this.saveButton = menu.findItem(R.id.save);
        conditionallyShowMoveButton();
        conditionallyEnableSaveButton();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                viewModel.saveWaypoint();
                finish();
                return true;
            case R.id.move:
                Bundle b = new Bundle();
                b.putString(MapActivity.BUNDLE_WAYPOINT_ID, String.valueOf(this.viewModel.getWaypoint().getId()));
                navigator.startActivity(MapActivity.class, b);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void conditionallyShowMoveButton() {
        if (moveButton != null) {
            moveButton.setVisible(viewModel.getWaypoint().getId() != 0);
        }
    }

    private void conditionallyEnableSaveButton() {
        if (saveButton != null) {
            saveButton.setEnabled(viewModel.canSaveWaypoint());
            saveButton.getIcon().setAlpha(viewModel.canSaveWaypoint() ? 255 : 130);
        }
    }
}
