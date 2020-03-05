package org.nexttracks.android.ui.regions;

import org.nexttracks.android.data.WaypointModel;
import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;

import io.objectbox.query.Query;

public interface RegionsMvvm {

    interface View extends MvvmView {
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        Query<WaypointModel> getWaypointsList();
        void delete(WaypointModel model);

        void exportWaypoints();
    }
}
