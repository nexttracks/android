package org.owntracks.android.ui.map;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.greenrobot.eventbus.EventBus;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.owntracks.android.R;
import org.owntracks.android.databinding.UiMapBinding;
import org.owntracks.android.model.FusedContact;
import org.owntracks.android.services.BackgroundService;
import org.owntracks.android.services.LocationProcessor;
import org.owntracks.android.services.MessageProcessorEndpointHttp;
import org.owntracks.android.support.ContactImageProvider;
import org.owntracks.android.support.Events;
import org.owntracks.android.support.GeocodingProvider;
import org.owntracks.android.support.Runner;
import org.owntracks.android.support.widgets.BindingConversions;
import org.owntracks.android.ui.base.BaseActivity;
import org.owntracks.android.ui.welcome.WelcomeActivity;

import java.util.Locale;
import java.util.WeakHashMap;

import javax.inject.Inject;

import timber.log.Timber;

public class MapActivity extends BaseActivity<UiMapBinding, MapMvvm.ViewModel> implements MapMvvm.View, View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener, Observer {
    public static final String BUNDLE_KEY_CONTACT_ID = "BUNDLE_KEY_CONTACT_ID";
    private static final long ZOOM_LEVEL_STREET = 15;
    private final int PERMISSIONS_REQUEST_CODE = 1;

    private final WeakHashMap<String, Marker> mMarkers = new WeakHashMap<>();
    private MapView map;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private boolean isMapReady = false;
    private Menu mMenu;

    @Inject
    Runner runner;

    @Inject
    ContactImageProvider contactImageProvider;

    @Inject
    EventBus eventBus;

    @Inject
    protected GeocodingProvider geocodingProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (preferences.isFirstStart()) {
            navigator.startActivity(WelcomeActivity.class);
            finish();
        }

        bindAndAttachContentView(R.layout.ui_map, savedInstanceState);

        setSupportToolbar(this.binding.toolbar, false, true);
        setDrawer(this.binding.toolbar);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);

        IMapController mapController = map.getController();
        mapController.setZoom(15.0);

        map.setOnClickListener((MapViewModel) viewModel);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(map);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.disableFollowLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        map.getOverlays().add(myLocationOverlay);

        CopyrightOverlay mCopyrightOverlay = new CopyrightOverlay(this);
        map.getOverlays().add(mCopyrightOverlay);
        map.invalidate();
//        new Handler().postDelayed(() -> {
//            map.getOverlays().remove(mCopyrightOverlay);
//            map.invalidate();
//        }, 5000);

        isMapReady = true;
        viewModel.onMapReady();

        this.bottomSheetBehavior = BottomSheetBehavior.from(this.binding.bottomSheetLayout);
        this.binding.contactPeek.contactRow.setOnClickListener(this);
        this.binding.contactPeek.contactRow.setOnLongClickListener(this);
        this.binding.moreButton.setOnClickListener(this::showPopupMenu);
        setBottomSheetHidden();

        AppBarLayout appBarLayout = this.binding.appBarLayout;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

        viewModel.getContact().observe(this, this);
        viewModel.getBottomSheetHidden().observe(this, o -> {
            if((Boolean) o) {
                setBottomSheetHidden();
            } else {
                setBottomSheetCollapsed();
            }
        });
        viewModel.getCenter().observe(this, o -> {
            if(o != null) {
                mapController.setCenter((GeoPoint) o);
            }
        });
        checkAndRequestLocationPermissions();
        Timber.v("starting BackgroundService");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService((new Intent(this, BackgroundService.class)));
        } else {
            startService((new Intent(this, BackgroundService.class)));
        }
    }

    private void checkAndRequestLocationPermissions() {
        if (!requirementsChecker.isPermissionCheckPassed()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Activity currentActivity = this;
                    new AlertDialog.Builder(this).setCancelable(true).setMessage(R.string.permissions_description).setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE)).show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
                }
            }
        }
    }

    @Override
    public void onChanged(@Nullable Object activeContact) {
        if (activeContact != null) {
            FusedContact c = (FusedContact) activeContact;
            Timber.v("for contact: %s", c.getId());

            binding.contactPeek.name.setText(c.getFusedName());
            if(c.hasLocation()) {
                ContactImageProvider.setImageViewAsync(binding.contactPeek.image, c);
                GeocodingProvider.resolve(c.getMessageLocation(), binding.contactPeek.location);
                BindingConversions.setRelativeTimeSpanString(binding.contactPeek.locationDate, c.getTst());
                binding.acc.setText(String.format(Locale.getDefault(),"%s m",c.getFusedLocationAccuracy()));
                binding.tid.setText(c.getTrackerId());
                binding.id.setText(c.getId());
                if(viewModel.hasLocation()) {
                    binding.distance.setVisibility(View.VISIBLE);
                    binding.distanceLabel.setVisibility(View.VISIBLE);

                    float[] distance = new float[2];
                    Location.distanceBetween(viewModel.getCurrentLocation().getLatitude(), viewModel.getCurrentLocation().getLongitude(), c.getGeoPoint().getLatitude(), c.getGeoPoint().getLongitude(), distance);

                    binding.distance.setText(String.format(Locale.getDefault(),"%d m",Math.round(distance[0])));
                } else {
                    binding.distance.setVisibility(View.GONE);
                    binding.distanceLabel.setVisibility(View.GONE);

                }

            } else {
                binding.contactPeek.location.setText(R.string.na);
                binding.contactPeek.locationDate.setText(R.string.na);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onDestroy() {
        map.onDetach();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
            isMapReady = true;
            viewModel.onMapReady();
        }
        handleIntentExtras(getIntent());
    }

    private void handleIntentExtras(Intent intent) {
        Timber.v("handleIntentExtras");

        Bundle b = navigator.getExtrasBundle(intent);
        if (b != null) {
            Timber.v("intent has extras from drawerProvider");
            String contactId = b.getString(BUNDLE_KEY_CONTACT_ID);
            if (contactId != null) {
                viewModel.restore(contactId);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntentExtras(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_map, menu);
        this.mMenu = menu;
        if (!viewModel.hasLocation())
            enableLocationMenus();
        else
            disableLocationMenus();

        updateMonitoringModeMenu();

        return true;
    }

    public void updateMonitoringModeMenu() {
        MenuItem item = this.mMenu.findItem(R.id.menu_monitoring);

        switch (preferences.getMonitoring()) {
            case LocationProcessor.MONITORING_QUIET:
                item.setIcon(R.drawable.ic_stop_white_36dp);
                item.setTitle(R.string.monitoring_quiet);
                break;
            case LocationProcessor.MONITORING_MANUAL:
                item.setIcon(R.drawable.ic_pause_white_36dp);
                item.setTitle(R.string.monitoring_manual);
                break;
            case LocationProcessor.MONITORING_SIGNIFFICANT:
                item.setIcon(R.drawable.ic_play_white_36dp);
                item.setTitle(R.string.monitoring_signifficant);

                break;
            case LocationProcessor.MONITORING_MOVE:
                item.setIcon(R.drawable.ic_step_forward_2_white_36dp);
                item.setTitle(R.string.monitoring_move);
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_report) {
            viewModel.sendLocation();

            return true;
        } else if (itemId == R.id.menu_mylocation) {
            viewModel.onMenuCenterDeviceClicked();
            return true;
        } else if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.menu_monitoring) {
            stepMonitoringModeMenu();
        }
        return false;
    }

    private void stepMonitoringModeMenu() {
        preferences.setMonitoringNext();

        int newmode = preferences.getMonitoring();
        if(newmode == LocationProcessor.MONITORING_QUIET) {
            Toast.makeText(this, R.string.monitoring_quiet, Toast.LENGTH_SHORT).show();
        }else if (newmode == LocationProcessor.MONITORING_MANUAL)  {
            Toast.makeText(this, R.string.monitoring_manual, Toast.LENGTH_SHORT).show();
        } else if (newmode == LocationProcessor.MONITORING_SIGNIFFICANT)  {
            Toast.makeText(this, R.string.monitoring_signifficant, Toast.LENGTH_SHORT).show();
        } else  {
            Toast.makeText(this, R.string.monitoring_move, Toast.LENGTH_SHORT).show();
        }

    }

    private void disableLocationMenus() {
        if (this.mMenu != null) {
            this.mMenu.findItem(R.id.menu_mylocation).getIcon().setAlpha(130);
            this.mMenu.findItem(R.id.menu_report).getIcon().setAlpha(130);
        }
    }

    public void enableLocationMenus() {
        if (this.mMenu != null) {
            this.mMenu.findItem(R.id.menu_mylocation).getIcon().setAlpha(255);
            this.mMenu.findItem(R.id.menu_report).getIcon().setAlpha(255);
        }
    }

    @Override
    public void clearMarkers() {
        if (isMapReady)
            for (Marker m : mMarkers.values()) {
                m.remove(map);
            }
        mMarkers.clear();
    }

    @Override
    public void removeMarker(@Nullable FusedContact contact) {
        if(contact == null)
            return;

        Marker m = mMarkers.get(contact.getId());
        if(m != null)
            m.remove(map);
    }

    @Override
    public Marker getMarker(@Nullable FusedContact contact) {
        if(contact == null)
            return null;

        return mMarkers.get(contact.getId());
    }

    @Override
    public void updateMarker(@Nullable FusedContact contact) {
        if (contact == null || !contact.hasLocation() || !isMapReady) {
            Timber.v("unable to update marker. null:%s, location:%s, mapReady:%s",contact == null, contact == null || contact.hasLocation(), isMapReady);
            return;
        }

        Timber.v("updating marker for contact: %s", contact.getId());
        Marker m = mMarkers.get(contact.getId());

        if (m == null){
            m = new Marker(map, null);
            m.setTitle(contact.getId());
            m.setAnchor(0.0f, 0.0f);
            map.getOverlays().add(m);
            mMarkers.put(contact.getId(), m);
        } else {
            this.map.invalidate();
        }
        m.setPosition(contact.getGeoPoint());

        contactImageProvider.setMarkerAsync(m, contact);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_navigate:

                FusedContact c = viewModel.getActiveContact();
                if (c != null && c.hasLocation()) {
                    try {
                        GeoPoint l = c.getGeoPoint();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + l.getLatitude() + "," + l.getLongitude()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, getString(R.string.noNavigationApp), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.contactLocationUnknown), Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.menu_clear:
                viewModel.onClearContactClicked();
            default:
                return false;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        viewModel.onBottomSheetLongClick();
        return true;
    }

    @Override
    public void setBottomSheetExpanded() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    // BOTTOM SHEET CALLBACKS
    @Override
    public void onClick(View view) {
        viewModel.onBottomSheetClick();
    }

    @Override
    public void setBottomSheetCollapsed() {
        Timber.v("vm contact: %s", binding.getVm().getActiveContact());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void setBottomSheetHidden() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        if(mMenu != null)
            mMenu.close();
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v, Gravity.START); //new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_contacts, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        if (preferences.getModeId() == MessageProcessorEndpointHttp.MODE_ID)
            popupMenu.getMenu().removeItem(R.id.menu_clear);
        popupMenu.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            eventBus.postSticky(new Events.PermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION));
        }
    }
}
