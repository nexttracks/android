package org.nexttracks.android.ui.regions;

import androidx.annotation.NonNull;
import android.view.View;

import org.nexttracks.android.BR;
import org.nexttracks.android.R;
import org.nexttracks.android.data.WaypointModel;
import org.nexttracks.android.ui.base.BaseAdapter;
import org.nexttracks.android.ui.base.BaseAdapterItemView;

import java.util.List;

import io.objectbox.reactive.DataObserver;


class RegionsAdapter extends BaseAdapter<WaypointModel> implements DataObserver<List<WaypointModel>> {
    RegionsAdapter(ClickListener clickListener) {
        super(BaseAdapterItemView.of(BR.waypoint, R.layout.ui_row_region));
        setClickListener(clickListener);
    }

    @Override
    public void onData(@NonNull List<WaypointModel> data) {
        setItems(data);
    }

    interface ClickListener extends BaseAdapter.ClickListener<WaypointModel> {
        void onClick(@NonNull WaypointModel object, @NonNull View view, boolean longClick);
    }
}
