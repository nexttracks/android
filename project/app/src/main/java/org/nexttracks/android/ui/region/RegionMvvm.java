package org.nexttracks.android.ui.region;

import org.nexttracks.android.data.WaypointModel;
import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;

import androidx.databinding.Bindable;

public interface RegionMvvm {

    interface View extends MvvmView {
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        void loadWaypoint(long waypointId);

        @Bindable WaypointModel getWaypoint();
        boolean canSaveWaypoint();
        void saveWaypoint();
    }
}
