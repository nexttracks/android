package org.nexttracks.android.ui.map;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.nexttracks.android.data.repos.ContactsRepo;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.messages.MessageClear;
import org.nexttracks.android.messages.MessageLocation;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.services.LocationProcessor;
import org.nexttracks.android.services.MessageProcessor;
import org.nexttracks.android.support.Events;
import org.nexttracks.android.ui.base.viewmodel.BaseViewModel;

import java.util.Collection;

import javax.inject.Inject;

import timber.log.Timber;

@PerActivity
public class MapViewModel extends BaseViewModel<MapMvvm.View> implements MapMvvm.ViewModel<MapMvvm.View>, MapView.OnClickListener, Marker.OnMarkerClickListener {
    private final ContactsRepo contactsRepo;
    private final LocationProcessor locationProcessor;
    private FusedContact activeContact;
    private MessageProcessor messageProcessor;
    private Location mLocation;

    private static final int VIEW_FREE = 0;
    private static final int VIEW_CONTACT = 1;
    private static final int VIEW_DEVICE = 2;


    private static int mode = VIEW_DEVICE;
    private MutableLiveData<FusedContact> liveContact = new MutableLiveData<>();
    private MutableLiveData<Boolean> liveBottomSheetHidden = new MutableLiveData<>();
    private MutableLiveData<GeoPoint> liveCamera = new MutableLiveData<>();

    @Inject
    public MapViewModel(ContactsRepo contactsRepo, LocationProcessor locationRepo, MessageProcessor messageProcessor) {
        Timber.v("onCreate");
        this.contactsRepo = contactsRepo;
        this.messageProcessor = messageProcessor;
        this.locationProcessor = locationRepo;
    }

    @Override
    public void saveInstanceState(@NonNull Bundle outState) {
    }

    @Override
    public void restoreInstanceState(@NonNull Bundle savedInstanceState) {
    }

    @Override
    public void onMapReady() {
        for(Object c : contactsRepo.getAllAsList()) {
            getView().updateContact((FusedContact) c);
        }

        if(mode == VIEW_CONTACT && activeContact != null)
            setViewModeContact(activeContact, true);
        else if (mode == VIEW_FREE) {
            setViewModeFree();
        } else {
            setViewModeDevice();
        }
    }

    @Override
    public LiveData<FusedContact> getContact() {
        return liveContact;
    }

    @Override
    public LiveData<Boolean> getBottomSheetHidden() {
        return liveBottomSheetHidden;
    }

    @Override
    public LiveData<GeoPoint> getCenter() {
        return liveCamera;
    }

    @Override
    public void sendLocation() {
        locationProcessor.publishLocationMessage(MessageLocation.REPORT_TYPE_USER);
    }


    private void setViewModeContact(@NonNull String contactId, boolean center) {
        FusedContact c = contactsRepo.getById(contactId);
        if(c != null)
            setViewModeContact(c, center);
        else
            Timber.e("contact not found %s, ", contactId);
    }

    private void setViewModeContact(@NonNull FusedContact c, boolean center) {
        mode = VIEW_CONTACT;
        Timber.v("contactId:%s, obj:%s ", c.getId(), activeContact);

        activeContact = c;

        liveContact.postValue(c);
        liveBottomSheetHidden.postValue(false);

        if(center)
            liveCamera.postValue(c.getGeoPoint());

    }

    private void setViewModeFree() {
        Timber.v("setting view mode: VIEW_FREE");
        mode = VIEW_FREE;
        clearActiveContact();
    }

    private void setViewModeDevice() {
        Timber.v("setting view mode: VIEW_DEVICE");

        mode = VIEW_DEVICE;
        clearActiveContact();
        if(hasLocation()) {
            liveCamera.postValue(getCurrentLocation());
        } else {
            Timber.e("no location available");
        }
    }

    @Override
    @Nullable
    public GeoPoint getCurrentLocation() {
        return mLocation != null ? new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude()) : null;
    }

    @Override
    @Bindable
    public FusedContact getActiveContact() {
        return activeContact;
    }

    @Override
    public Collection<FusedContact> getContacts() {
        return this.contactsRepo.getAllAsList();
    }

    @Override
    public void restore(@NonNull String contactId) {
        Timber.v("restoring contact id:%s", contactId);
        setViewModeContact(contactId, true);
    }

    @Override
    public boolean hasLocation() {
        return mLocation != null;
    }


    private void clearActiveContact() {
        activeContact = null;
        liveContact.postValue(null);
        liveBottomSheetHidden.postValue(true);
    }

    @Override
    public void onBottomSheetClick() {
        getView().setBottomSheetExpanded();
    }

    @Override
    public void onMenuCenterDeviceClicked() {
        setViewModeDevice();
    }


    @Override
    public void onClearContactClicked() {
        MessageClear m = new MessageClear();
        if(activeContact != null) {
            m.setTopic(activeContact.getId());
            messageProcessor.queueMessageForSending(m);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.FusedContactAdded e) {
        onEvent(e.getContact());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.FusedContactRemoved c) {
        if(c.getContact() == activeContact) {
            clearActiveContact();
            setViewModeFree();
        }
        getView().removeContact(c.getContact());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FusedContact c) {
        getView().updateContact(c);
        Marker m = getView().getContact(c);
        if (m != null) {
            m.setOnMarkerClickListener(this);
        }
        if(c == activeContact) {
            liveContact.postValue(c);
            liveCamera.postValue(c.getGeoPoint());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.ModeChanged e) {
        getView().clearContacts();
        clearActiveContact();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.MonitoringChanged e) {
        getView().updateMonitoringModeMenu();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void onEvent(@NonNull Location l) {
        Timber.v("location source updated");

        this.mLocation = l;
//        if (mListener != null) {
//            this.mListener.onLocationChanged(this.mLocation);
//        }
        if(mode == VIEW_DEVICE) {
            liveCamera.postValue(getCurrentLocation());
        }
        getView().enableLocationMenus();
    }

    // Map Callback
//    @Override
//    public void activate( onLocationChangedListener) {
//       Timber.v("location source activated");
//       mListener = onLocationChangedListener;
//       if (mLocation != null)
//           this.mListener.onLocationChanged(mLocation);
//    }

    // Map Callback
//    @Override
//    public void deactivate() {
//        mListener = null;
//    }

    // Map Callback
    @Override
    public void onClick(View v) {
        setViewModeFree();
    }

    // Map Callback
    @Override
    public boolean onMarkerClick(Marker marker, MapView m) {
//        Toast.makeText((MapActivity)this.getView(), "Test", Toast.LENGTH_SHORT).show();
        if (marker.getTitle() != null) {
            setViewModeContact(marker.getTitle(), false);
        }
        return true;
    }

    @Override
    public void onBottomSheetLongClick() {
        setViewModeContact(activeContact.getId(), true);
    }

}
