package org.nexttracks.android.ui.map;

import androidx.lifecycle.LiveData;
import androidx.databinding.Bindable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.ui.base.view.MvvmView;
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel;

import java.util.Collection;

public interface MapMvvm {

    interface View extends MvvmView {
        void setBottomSheetExpanded();
        void setBottomSheetCollapsed();
        void setBottomSheetHidden();

        Marker getMarker(FusedContact contact);
        void updateMarker(FusedContact contact);
        void removeMarker(FusedContact c);
        void clearMarkers();
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
        boolean hasLocation();
        void onMapReady();

        LiveData<FusedContact> getContact();
        LiveData<Boolean> getBottomSheetHidden();
        LiveData<GeoPoint> getCenter();

        void sendLocation();
    }
}
