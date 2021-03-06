package org.nexttracks.android.ui.map;

import androidx.lifecycle.LiveData;
import androidx.databinding.Bindable;

import org.nexttracks.android.data.WaypointModel;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;
import org.osmdroid.views.overlay.Polygon;

import java.util.Collection;

public interface MapMvvm {

    interface View extends MvvmView {
        void setBottomSheetExpanded();
        void setBottomSheetCollapsed();
        void setBottomSheetHidden();

        Marker getContact(FusedContact contact);
        void updateContact(FusedContact contact);
        void removeContact(FusedContact contact);
        void clearContacts();
        DraggablePolygon getWaypoint(WaypointModel waypoint);
        void updateWaypoint(WaypointModel waypoint);
        void removeWaypoint(WaypointModel waypoint);
        void clearWaypoints();
        void enableLocationMenus();
        void updateMonitoringModeMenu();
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V>  {
        GeoPoint getCurrentLocation();

        @Bindable
        FusedContact getActiveContact();
        Collection<FusedContact> getContacts();

        void onBottomSheetLongClick();
        void onBottomSheetClick();
        void onMenuCenterDeviceClicked();
        void onClearContactClicked();

        void restore(String contactId);
        void drag(long waypointId);
        void undrag();
        WaypointModel getDraggedWaypoint();
        boolean hasLocation();
        void onMapReady();

        LiveData<FusedContact> getContact();
        LiveData<Boolean> getBottomSheetHidden();
        LiveData<GeoPoint> getCenter();

        void sendLocation();
    }
}
