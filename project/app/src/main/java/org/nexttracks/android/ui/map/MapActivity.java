package org.nexttracks.android.ui.map;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import org.nexttracks.android.R;
import org.nexttracks.android.data.WaypointModel;
import org.nexttracks.android.databinding.UiMapBinding;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.services.BackgroundService;
import org.nexttracks.android.services.LocationProcessor;
import org.nexttracks.android.services.MessageProcessorEndpointHttp;
import org.nexttracks.android.support.ContactImageProvider;
import org.nexttracks.android.support.Events;
import org.nexttracks.android.support.GeocodingProvider;
import org.nexttracks.android.support.RunThingsOnOtherThreads;
import org.nexttracks.android.support.widgets.BindingConversions;
import org.nexttracks.android.ui.base.BaseActivity;
import org.nexttracks.android.ui.welcome.WelcomeActivity;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;


import javax.inject.Inject;

import timber.log.Timber;

public class MapActivity extends BaseActivity<UiMapBinding, MapMvvm.ViewModel> implements MapMvvm.View, View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener, Observer {
    public static final String BUNDLE_KEY_CONTACT_ID = "BUNDLE_KEY_CONTACT_ID";
    public static final String BUNDLE_WAYPOINT_ID = "BUNDLE_WAYPOINT_ID";
    private static final long ZOOM_LEVEL_STREET = 15;
    private final int PERMISSIONS_REQUEST_CODE = 1;

    private final WeakHashMap<String, Marker> mContacts = new WeakHashMap<>();
    public final WeakHashMap<Long, DraggablePolygon> mWaypoints = new WeakHashMap<>();
    private MapView map;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private boolean isMapReady = false;
    private Menu mMenu;
    private MenuItem doneButton;

    @Inject
    RunThingsOnOtherThreads runThingsOnOtherThreads;

    @Inject
    ContactImageProvider contactImageProvider;

    @Inject
    EventBus eventBus;

    @Inject
    protected GeocodingProvider geocodingProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!preferences.isSetupCompleted()) {
            navigator.startActivity(WelcomeActivity.class);
            finish();
        }

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        try {
            bindAndAttachContentView(R.layout.ui_map, savedInstanceState);
        } catch(SQLiteCantOpenDatabaseException e) {
            Timber.e(e);
        }

        setSupportToolbar(this.binding.toolbar, false, true);
        setDrawer(this.binding.toolbar);

        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);

        IMapController mapController = map.getController();
        mapController.setZoom(15.0);

        map.setOnClickListener((MapViewModel) viewModel);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(map);
        // TODO. is there another way to do this?
        try {
            Field circlePaintField = MyLocationNewOverlay.class.getDeclaredField("mCirclePaint");
            circlePaintField.setAccessible(true);
            Paint circlePaint = (Paint) circlePaintField.get(myLocationOverlay);
            if (circlePaint != null) {
                circlePaint.setColor(getResources().getColor(R.color.myLocationCircle));
                circlePaintField.set(myLocationOverlay, circlePaint);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Bitmap myLocationIcon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_mylocation);
        myLocationOverlay.setPersonHotspot(myLocationIcon.getWidth() / 2.0f,myLocationIcon.getHeight() / 2.0f);
        myLocationOverlay.setPersonIcon(myLocationIcon);
        myLocationOverlay.setDirectionArrow(myLocationIcon, myLocationIcon);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.disableFollowLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        map.getOverlays().add(myLocationOverlay);
        map.setMinZoomLevel(2.5);

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
                List<String> permissions = new ArrayList<String>();
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                }
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Activity currentActivity = this;
                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    new AlertDialog.Builder(this).setCancelable(true).setMessage(R.string.permissions_description).setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(currentActivity, permissions.toArray(new String[0]), PERMISSIONS_REQUEST_CODE)).show();
                } else {
                    ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
                }
            }
        }
    }

    private String formatMeters(float meters) {
        if (meters < 1000) {
            return String.format(Locale.getDefault(), "%.0f%nm", meters);
        } else if (meters < 50000) {
            return String.format(Locale.getDefault(), "%.1f%nkm", meters / 1000);
        } else {
            return String.format(Locale.getDefault(), "%.0f%nkm", meters / 1000);
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
                GeocodingProvider.displayFusedLocationInViewAsync(binding.contactPeek.location, c, c.getMessageLocation());
                BindingConversions.setRelativeTimeSpanString(binding.contactPeek.locationDate, c.getTst());
                GeocodingProvider.resolve(c.getMessageLocation(), binding.location, false);
                binding.acc.setText(formatMeters(Float.parseFloat(c.getFusedLocationAccuracy())));
                binding.tid.setText(c.getTrackerId());
                binding.id.setText(c.getId());
                if(viewModel.hasLocation()) {
                    binding.distance.setVisibility(View.VISIBLE);
                    binding.distanceLabel.setVisibility(View.VISIBLE);

                    float[] distance = new float[2];
                    Location.distanceBetween(viewModel.getCurrentLocation().getLatitude(), viewModel.getCurrentLocation().getLongitude(), c.getGeoPoint().getLatitude(), c.getGeoPoint().getLongitude(), distance);

                    binding.distance.setText(formatMeters(distance[0]));
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
                return;
            }
            String waypointId = b.getString(BUNDLE_WAYPOINT_ID);
            if (waypointId != null) {
                viewModel.drag(Long.parseLong(waypointId));
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
        this.doneButton = menu.findItem(R.id.done);
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
            case LocationProcessor.MONITORING_SIGNIFICANT:
                item.setIcon(R.drawable.ic_play_white_36dp);
                item.setTitle(R.string.monitoring_significant);

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
        if (itemId == R.id.done) {
            viewModel.undrag();
            return true;
        } else if (itemId == R.id.menu_report) {
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
        } else if (newmode == LocationProcessor.MONITORING_SIGNIFICANT)  {
            Toast.makeText(this, R.string.monitoring_significant, Toast.LENGTH_SHORT).show();
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
    public void clearContacts() {
        if (isMapReady) {
            for (Marker m : mContacts.values()) {
                m.remove(map);
            }
        }
        mContacts.clear();
    }

    @Override
    public void removeContact(@Nullable FusedContact contact) {
        if(contact == null)
            return;

        Marker m = mContacts.get(contact.getId());
        if(m != null)
            m.remove(map);
    }

    @Override
    public Marker getContact(@Nullable FusedContact contact) {
        if(contact == null)
            return null;

        return mContacts.get(contact.getId());
    }

    @Override
    public void updateContact(@Nullable FusedContact contact) {
        if (contact == null || !contact.hasLocation() || !isMapReady) {
            Timber.v("unable to update contact. null:%s, location:%s, mapReady:%s",contact == null, contact == null || contact.hasLocation(), isMapReady);
            return;
        }

        Timber.v("updating contact: %s", contact.getId());
        Marker m = mContacts.get(contact.getId());

        if (m == null){
            m = new Marker(map, null);
            m.setTitle(contact.getId());
            m.setAnchor(0.5f, 0.5f);
            map.getOverlays().add(m);
            mContacts.put(contact.getId(), m);
        } else {
            this.map.invalidate();
        }
        m.setPosition(contact.getGeoPoint());

        contactImageProvider.setMarkerAsync(m, contact);
    }

    @Override
    public void clearWaypoints() {
        if (isMapReady) {
            for (Polygon p : mWaypoints.values()) {
                map.getOverlayManager().remove(p);
            }
        }
        mWaypoints.clear();
    }

    @Override
    public void removeWaypoint(@Nullable WaypointModel waypoint) {
        if(waypoint == null)
            return;

        Polygon p = mWaypoints.get(waypoint.getId());
        Timber.v("removing waypoint: %s", waypoint.getDescription());
        if(p != null) {
            map.getOverlayManager().remove(p);
        }
    }

    @Override
    public DraggablePolygon getWaypoint(@Nullable WaypointModel waypoint) {
        if(waypoint == null)
            return null;

        return mWaypoints.get(waypoint.getId());
    }

    @Override
    public void updateWaypoint(@Nullable WaypointModel waypoint) {
        if (waypoint == null || !waypoint.hasGeofence() || !isMapReady) {
            Timber.v("unable to update waypoint. null:%s, geofence:%s, mapReady:%s",waypoint == null, waypoint == null || waypoint.hasGeofence(), isMapReady);
            return;
        }

        Timber.v("updating waypoint: %s", waypoint.getDescription());
        DraggablePolygon p = mWaypoints.get(waypoint.getId());

        if (p == null) {
            p = new DraggablePolygon(map, waypoint.getGeofenceRadius());
            p.setOnClickListener((polygon, mapView, eventPos) -> false);
            colorWaypoint(p, R.color.primary);
            map.getOverlays().add(p);
            mWaypoints.put(waypoint.getId(), p);
        } else {
            this.map.invalidate();
        }
        double lat = waypoint.getGeofenceLatitude();
        double lon = waypoint.getGeofenceLongitude();
        p.setPosition(new GeoPoint(lat, lon), waypoint.getGeofenceRadius());
    }

    public void colorWaypoint(@NonNull DraggablePolygon polygon, int color) {
        polygon.getFillPaint().setColor(getResources().getColor(color));
        polygon.getFillPaint().setAlpha(120);

        polygon.getOutlinePaint().setColor(getResources().getColor(color));
        polygon.getOutlinePaint().setStrokeWidth(3f);
        polygon.getOutlinePaint().setAlpha(210);
    }

    public void setMapDraggable(boolean draggable) {
        if (draggable) {
            map.setOnTouchListener((view, motionEvent) -> false);
            map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        } else {
            map.setOnTouchListener((view, motionEvent) -> {
                this.getWaypoint(viewModel.getDraggedWaypoint()).onTouchEvent(motionEvent, (MapView) view);
                return true;
            });
            map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        }
    }

    public MenuItem getDoneButton() {
        return doneButton;
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
        if (preferences.getMode() == MessageProcessorEndpointHttp.MODE_ID)
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
