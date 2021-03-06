package org.nexttracks.android.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mapzen.android.lost.api.FusedLocationProviderApi;
import com.mapzen.android.lost.api.Geofence;
import com.mapzen.android.lost.api.GeofencingApi;
import com.mapzen.android.lost.api.GeofencingEvent;
import com.mapzen.android.lost.api.GeofencingRequest;
import com.mapzen.android.lost.api.LocationAvailability;
import com.mapzen.android.lost.api.LocationCallback;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationResult;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.nexttracks.android.R;
import org.nexttracks.android.data.WaypointModel;
import org.nexttracks.android.data.repos.ContactsRepo;
import org.nexttracks.android.data.repos.LocationRepo;
import org.nexttracks.android.data.repos.WaypointsRepo;
import org.nexttracks.android.messages.MessageLocation;
import org.nexttracks.android.messages.MessageTransition;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.services.worker.Scheduler;
import org.nexttracks.android.support.DateFormatter;
import org.nexttracks.android.support.Events;
import org.nexttracks.android.support.GeocodingProvider;
import org.nexttracks.android.support.Preferences;
import org.nexttracks.android.support.RunThingsOnOtherThreads;
import org.nexttracks.android.support.ServiceBridge;
import org.nexttracks.android.support.preferences.OnModeChangedPreferenceChangedListener;
import org.nexttracks.android.ui.map.MapActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.DaggerService;
import timber.log.Timber;

public class BackgroundService extends DaggerService implements LostApiClient.ConnectionCallbacks, OnModeChangedPreferenceChangedListener, ServiceBridge.ServiceBridgeInterface {
    private static final int INTENT_REQUEST_CODE_LOCATION = 1263;
    private static final int INTENT_REQUEST_CODE_GEOFENCE = 1264;
    private static final int INTENT_REQUEST_CODE_CLEAR_EVENTS = 1263;

    private static final int NOTIFICATION_ID_ONGOING = 1;
    private static final String NOTIFICATION_CHANNEL_ONGOING = "O";

    private static final int NOTIFICATION_ID_EVENT_GROUP = 2;
    public static final String NOTIFICATION_CHANNEL_EVENTS = "E";

    private static int notificationEventsID = 3;

    private final String NOTIFICATION_GROUP_EVENTS = "events";

    // NEW ACTIONS ALSO HAVE TO BE ADDED TO THE SERVICE INTENT FILTER
    private static final String INTENT_ACTION_CLEAR_NOTIFICATIONS = "org.nexttracks.android.CLEAR_NOTIFICATIONS";
    private static final String INTENT_ACTION_SEND_LOCATION_USER = "org.nexttracks.android.SEND_LOCATION_USER";
    private static final String INTENT_ACTION_SEND_EVENT_CIRCULAR = "org.nexttracks.android.SEND_EVENT_CIRCULAR";
    private static final String INTENT_ACTION_REREQUEST_LOCATION_UPDATES = "org.nexttracks.android.REREQUEST_LOCATION_UPDATES";
    private static final String INTENT_ACTION_CHANGE_MONITORING = "org.nexttracks.android.CHANGE_MONITORING";

    private LostApiClient lostApiClient;
    private FusedLocationProviderApi mFusedLocationClient;
    private GeofencingApi mGeofencingClient;

    private LocationCallback locationCallback;
    private LocationCallback locationCallbackOnDemand;
    private MessageLocation lastLocationMessage;
    private MessageProcessor.EndpointState lastEndpointState = MessageProcessor.EndpointState.INITIAL;


    private NotificationCompat.Builder activeNotificationCompatBuilder;
    private NotificationCompat.Builder eventsNotificationCompatBuilder;
    private NotificationManager notificationManager;

    private NotificationManagerCompat notificationManagerCompat;

    private final LinkedList<Spannable> activeNotifications = new LinkedList<>();
    private int lastQueueLength = 0;
    private Notification stackNotification;

    @Inject
    Preferences preferences;

    @Inject
    EventBus eventBus;

    @Inject
    Scheduler scheduler;

    @Inject
    LocationProcessor locationProcessor;

    @Inject
    GeocodingProvider geocodingProvider;

    @Inject
    ContactsRepo contactsRepo;

    @Inject
    LocationRepo locationRepo;

    @Inject
    RunThingsOnOtherThreads runThingsOnOtherThreads;

    @Inject
    WaypointsRepo waypointsRepo;

    @Inject
    ServiceBridge serviceBridge;
    private boolean connected = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.v("Background service onCreate. ThreadID: %s", Thread.currentThread());
        serviceBridge.bind(this);
        lostApiClient = new LostApiClient.Builder(this).addConnectionCallbacks(this).build();
        lostApiClient.connect();
        mFusedLocationClient = LocationServices.FusedLocationApi;
        mGeofencingClient = LocationServices.GeofencingApi;
        notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Timber.i("Locationresult received: %s", locationResult);
                onLocationChanged(locationResult.getLastLocation(), MessageLocation.REPORT_TYPE_DEFAULT);
            }
        };

        locationCallbackOnDemand = new LocationCallback() {
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Timber.i("Ondemand Locationresult received: %s", locationResult);
                Timber.tag("location").i("Ondemand Locationresult received: %s", locationResult);
                onLocationChanged(locationResult.getLastLocation(), MessageLocation.REPORT_TYPE_RESPONSE);
            }
        };

        setupNotificationChannels();
        startForeground(NOTIFICATION_ID_ONGOING, getOngoingNotification());

        setupLocationRequest();

        scheduler.scheduleLocationPing();

        setupGeofences();

        eventBus.register(this);
        eventBus.postSticky(new Events.ServiceStarted());

        preferences.registerOnPreferenceChangedListener(this);

    }


    @Override
    public void onDestroy() {
        stopForeground(true);
        preferences.unregisterOnPreferenceChangedListener(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            handleIntent(intent);
        }

        return START_STICKY;
    }

    private void handleIntent(@NonNull Intent intent) {

        if (intent.getAction() != null) {
            Timber.v("intent received with action:%s", intent.getAction());

            switch (intent.getAction()) {
                case INTENT_ACTION_SEND_LOCATION_USER:
                    locationProcessor.publishLocationMessage(MessageLocation.REPORT_TYPE_USER);
                    return;
                case INTENT_ACTION_SEND_EVENT_CIRCULAR:
                    onGeofencingEvent(GeofencingEvent.fromIntent(intent));
                    return;
                case INTENT_ACTION_CLEAR_NOTIFICATIONS:
                    clearEventStackNotification();
                    return;
                case INTENT_ACTION_REREQUEST_LOCATION_UPDATES:
                    setupLocationRequest();
                    return;
                case INTENT_ACTION_CHANGE_MONITORING:
                    if(intent.hasExtra(preferences.getPreferenceKey(R.string.preferenceKeyMonitoring))) {
                        preferences.setMonitoring(intent.getIntExtra(preferences.getPreferenceKey(R.string.preferenceKeyMonitoring), preferences.getMonitoring()));
                    } else {
                        // Step monitoring mode if no mode is specified
                        preferences.setMonitoringNext();
                    }
                    return;
                default:
                    Timber.v("unhandled intent action received: %s", intent.getAction());
            }
        }
    }

    private void setupNotificationChannels() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        // Importance min will show normal priority notification for foreground service. See https://developer.android.com/reference/android/app/NotificationManager#IMPORTANCE_MIN
        // User has to actively configure this in the notification channel settings.
        NotificationChannel ongoingChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ONGOING, getString(R.string.notificationChannelOngoing), NotificationManager.IMPORTANCE_DEFAULT);
        ongoingChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        ongoingChannel.setDescription(getString(R.string.notificationChannelOngoingDescription));
        ongoingChannel.enableLights(false);
        ongoingChannel.enableVibration(false);
        ongoingChannel.setShowBadge(false);
        ongoingChannel.setSound(null, null);
        notificationManager.createNotificationChannel(ongoingChannel);

        NotificationChannel eventsChannel = new NotificationChannel(NOTIFICATION_CHANNEL_EVENTS, getString(R.string.events), NotificationManager.IMPORTANCE_HIGH);
        eventsChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        eventsChannel.setDescription(getString(R.string.notificationChannelEventsDescription));
        eventsChannel.enableLights(false);
        eventsChannel.enableVibration(false);
        eventsChannel.setShowBadge(true);
        eventsChannel.setSound(null, null);
        notificationManager.createNotificationChannel(eventsChannel);
    }

    @Nullable
    private NotificationCompat.Builder getOngoingNotificationBuilder() {
        if (activeNotificationCompatBuilder != null)
            return activeNotificationCompatBuilder;


        activeNotificationCompatBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ONGOING);


        Intent resultIntent = new Intent(this, MapActivity.class);
        resultIntent.setAction("android.intent.action.MAIN");
        resultIntent.addCategory("android.intent.category.LAUNCHER");
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        activeNotificationCompatBuilder.setContentIntent(resultPendingIntent);
        activeNotificationCompatBuilder.setSortKey("a");


        Intent publishIntent = new Intent();
        publishIntent.setAction(INTENT_ACTION_SEND_LOCATION_USER);
        PendingIntent publishPendingIntent = PendingIntent.getService(this, 0, publishIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent changeMonitoringIntent = new Intent();
        publishIntent.setAction(INTENT_ACTION_CHANGE_MONITORING);
        PendingIntent changeMonitoringPendingIntent = PendingIntent.getService(this, 0, publishIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        activeNotificationCompatBuilder.addAction(R.drawable.ic_report_notification, getString(R.string.publish), publishPendingIntent).addAction(R.drawable.ic_report_notification, getString(R.string.notificationChangeMonitoring), changeMonitoringPendingIntent);
        activeNotificationCompatBuilder.setSmallIcon(R.drawable.ic_notification);
        activeNotificationCompatBuilder.setPriority(preferences.getNotificationHigherPriority() ? NotificationCompat.PRIORITY_DEFAULT : NotificationCompat.PRIORITY_MIN);
        activeNotificationCompatBuilder.setSound(null, AudioManager.STREAM_NOTIFICATION);


        if (android.os.Build.VERSION.SDK_INT >= 23) {
            activeNotificationCompatBuilder.setColor(getColor(R.color.primary));
            activeNotificationCompatBuilder.setCategory(NotificationCompat.CATEGORY_SERVICE);
            activeNotificationCompatBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        activeNotificationCompatBuilder.setOngoing(true);

        return activeNotificationCompatBuilder;
    }

    private void updateOngoingNotification() {
        notificationManager.notify(NOTIFICATION_ID_ONGOING, getOngoingNotification());
    }

    private Notification getOngoingNotification() {
        NotificationCompat.Builder builder = getOngoingNotificationBuilder();

        if (builder == null)
            return null;


        if (this.lastLocationMessage != null && preferences.getNotificationLocation()) {
            builder.setContentTitle(this.lastLocationMessage.getGeocoder().convert(false));
            builder.setWhen(TimeUnit.SECONDS.toMillis(this.lastLocationMessage.getTst()));
            builder.setNumber(lastQueueLength);
        } else {
            builder.setContentTitle(getString(R.string.app_name));
        }

        // Show monitoring mode if endpoint state is not interesting
        if (lastEndpointState == MessageProcessor.EndpointState.CONNECTED || lastEndpointState == MessageProcessor.EndpointState.IDLE) {
            builder.setContentText(getMonitoringLabel(preferences.getMonitoring()));
        } else if (lastEndpointState == MessageProcessor.EndpointState.ERROR && lastEndpointState.getMessage() != null) {
            builder.setContentText(lastEndpointState.getLabel(this) + ": " + lastEndpointState.getMessage());
        } else {
            builder.setContentText(lastEndpointState.getLabel(this));
        }
        return builder.build();
    }


    private String getMonitoringLabel(int mode) {
        switch (mode) {
            case LocationProcessor.MONITORING_QUIET:
                return getString(R.string.monitoring_quiet);
            case LocationProcessor.MONITORING_MANUAL:
                return getString(R.string.monitoring_manual);
            case LocationProcessor.MONITORING_SIGNIFICANT:
                return getString(R.string.monitoring_significant);
            case LocationProcessor.MONITORING_MOVE:
                return getString(R.string.monitoring_move);
        }
        return getString(R.string.na);
    }

    private void sendEventNotification(MessageTransition message) {
        NotificationCompat.Builder builder = getEventsNotificationBuilder();

        if (builder == null) {
            Timber.e("no builder returned");
            return;
        }

        FusedContact c = contactsRepo.getById(message.getContactKey());

        long when = TimeUnit.SECONDS.toMillis(message.getTst());
        String location = message.getDesc();

        if (location == null) {
            location = getString(R.string.aLocation);
        }
        String title = message.getTid();
        if (c != null)
            title = c.getFusedName();
        else if (title == null) {
            title = message.getContactKey();
        }

        String text = String.format("%s %s", getString(message.getTransition() == Geofence.GEOFENCE_TRANSITION_ENTER ? R.string.transitionEntering : R.string.transitionLeaving), location);


        eventsNotificationCompatBuilder.setContentTitle(title);
        eventsNotificationCompatBuilder.setContentText(text);
        eventsNotificationCompatBuilder.setWhen(TimeUnit.SECONDS.toMillis(message.getTst()));
        eventsNotificationCompatBuilder.setShowWhen(true);
        eventsNotificationCompatBuilder.setGroup(NOTIFICATION_GROUP_EVENTS);
        // Deliver notification
        Notification n = eventsNotificationCompatBuilder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sendEventStackNotification(title, text, when);
        } else {
            notificationManagerCompat.notify(notificationEventsID++, n);
        }
    }

    @RequiresApi(23)
    private void sendEventStackNotification(String title, String text, long when) {
        if (Build.VERSION.SDK_INT >= 23) {
            Timber.v("SDK_INT >= 23, building stack notification");

            // Append new notification to existing
            CharSequence[] cs = null;

            if (stackNotification != null) {
                cs = (CharSequence[]) stackNotification.extras.get(NotificationCompat.EXTRA_TEXT_LINES);
            }

            String whenStr = DateFormatter.formatDate(TimeUnit.MILLISECONDS.toSeconds((when)));

            Spannable newLine = new SpannableString(String.format("%s %s %s", whenStr, title, text));
            newLine.setSpan(new StyleSpan(Typeface.BOLD), 0, whenStr.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            activeNotifications.push(newLine);
            Timber.v("groupedNotifications: %s", activeNotifications.size());
            String summary = getResources().getQuantityString(R.plurals.notificationEventsTitle, activeNotifications.size(), activeNotifications.size());

            NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
            inbox.setSummaryText(summary);

            for (Spannable n : activeNotifications) {
                inbox.addLine(n);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_EVENTS)
                    .setContentTitle(getString(R.string.events))
                    .setContentText(summary)
                    .setGroup(NOTIFICATION_GROUP_EVENTS) // same as group of single notifications
                    .setGroupSummary(true)
                    .setColor(getColor(R.color.primary))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setNumber(activeNotifications.size())
                    .setStyle(inbox)
                    .setContentIntent(PendingIntent.getActivity(this, (int) System.currentTimeMillis() / 1000, new Intent(this, MapActivity.class), PendingIntent.FLAG_ONE_SHOT))
                    .setDeleteIntent(PendingIntent.getService(this, INTENT_REQUEST_CODE_CLEAR_EVENTS, (new Intent(this, BackgroundService.class)).setAction(INTENT_ACTION_CLEAR_NOTIFICATIONS), PendingIntent.FLAG_ONE_SHOT));

            stackNotification = builder.build();
            notificationManagerCompat.notify(NOTIFICATION_GROUP_EVENTS, NOTIFICATION_ID_EVENT_GROUP, stackNotification);

        }
    }

    private void clearEventStackNotification() {
        Timber.v("clearing notification stack");
        activeNotifications.clear();
    }

    private void onGeofencingEvent(@Nullable final GeofencingEvent event) {
        if (event == null) {
            Timber.e("geofencingEvent null");
            return;
        }

        if (event.hasError()) {
            Timber.e("geofencingEvent hasError: %s", event.getErrorCode());
            return;
        }

        final int transition = event.getGeofenceTransition();
        for (int index = 0; index < event.getTriggeringGeofences().size(); index++) {
            WaypointModel w = waypointsRepo.get(Long.parseLong(event.getTriggeringGeofences().get(index).getRequestId()));
            if (w == null) {
                Timber.e("waypoint id %s not found for geofence event", event.getTriggeringGeofences().get(index).getRequestId());
                continue;
            }
            locationProcessor.onWaypointTransition(w, event.getTriggeringLocation(), transition, MessageTransition.TRIGGER_CIRCULAR);
        }
    }

    private void onLocationChanged(@Nullable Location location, @Nullable String reportType) {
        if (location == null) {
            Timber.e("no location provided");
            return;
        }
        Timber.v("location update received: tst:%s, acc:%s, lat:%s, lon:%s type:%s", location.getTime(), location.getAccuracy(), location.getLatitude(), location.getLongitude(), reportType);

        if (location.getTime() > locationRepo.getCurrentLocationTime()) {
            locationProcessor.onLocationChanged(location, reportType);
        } else {
            Timber.v("Not re-sending message with same timestamp as last");
        }
    }

    @SuppressWarnings("MissingPermission")
    public void requestOnDemandLocationUpdate() {
        if (missingLocationPermission()) {
            Timber.e("missing location permission");
            return;
        }

        LocationRequest request = LocationRequest.create();

//        request.setNumUpdates(1);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        request.setExpirationDuration(TimeUnit.MINUTES.toMillis(1));

        Timber.d("On demand location request");
        mFusedLocationClient.requestLocationUpdates(lostApiClient, request, locationCallbackOnDemand, runThingsOnOtherThreads.getBackgroundLooper());
    }

    @SuppressWarnings("MissingPermission")
    private void setupLocationRequest() {
        if (missingLocationPermission()) {
            Timber.e("missing location permission");
            return;
        }

        if (!connected || mFusedLocationClient == null) {
            Timber.e("FusedLocationClient not available: connected: %b, mFusedLocationClient: %s", connected, mFusedLocationClient);
            return;
        }

        int monitoring = preferences.getMonitoring();

        LocationRequest request = LocationRequest.create();

        switch (monitoring) {
            case LocationProcessor.MONITORING_QUIET:
            case LocationProcessor.MONITORING_MANUAL:
                request.setInterval(TimeUnit.SECONDS.toMillis(preferences.getLocatorInterval()));
                request.setSmallestDisplacement(preferences.getLocatorDisplacement());
                request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                break;
            case LocationProcessor.MONITORING_SIGNIFICANT:
                request.setInterval(TimeUnit.SECONDS.toMillis(preferences.getLocatorInterval()));
                request.setSmallestDisplacement(preferences.getLocatorDisplacement());
                request.setPriority(getLocationRequestPriority());
                break;
            case LocationProcessor.MONITORING_MOVE:
                request.setInterval(TimeUnit.SECONDS.toMillis(preferences.getMoveModeLocatorInterval()));
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                break;
        }
        // TODO: look at how to not have to use this workaround
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Timber.d("location request params: mode %s, interval (s):%s, fastestInterval (s):%s, priority:%s, displacement (m):%s", monitoring, TimeUnit.MILLISECONDS.toSeconds(request.getInterval()), TimeUnit.MILLISECONDS.toSeconds(request.getFastestInterval()), request.getPriority(), request.getSmallestDisplacement());
        mFusedLocationClient.removeLocationUpdates(lostApiClient, locationCallback);
        mFusedLocationClient.requestLocationUpdates(lostApiClient, request, locationCallback, runThingsOnOtherThreads.getBackgroundLooper());
    }

    private int getLocationRequestPriority() {
        switch (preferences.getLocatorPriority()) {
            case 0:
                return LocationRequest.PRIORITY_NO_POWER;
            case 1:
                return LocationRequest.PRIORITY_LOW_POWER;
            case 3:
                return LocationRequest.PRIORITY_HIGH_ACCURACY;
            case 2:
            default:
                return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent geofeneIntent = new Intent(this, BackgroundService.class);
        geofeneIntent.setAction(INTENT_ACTION_SEND_EVENT_CIRCULAR);
        return PendingIntent.getBroadcast(this, INTENT_REQUEST_CODE_GEOFENCE, geofeneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @SuppressWarnings("MissingPermission")
    private void setupGeofences() {
        if (missingLocationPermission()) {
            Timber.e("missing location permission");
            return;
        }
        if (!connected || mGeofencingClient == null) {
            Timber.e("GeofencingClient not available: connected: %b, mGeofencingClient: %s", connected, mGeofencingClient);
            return;
        }

        Timber.d("loader thread:%s, isMain:%s", Looper.myLooper(), Looper.myLooper() == Looper.getMainLooper());

        LinkedList<Geofence> geofences = new LinkedList<>();
        List<WaypointModel> loadedWaypoints = waypointsRepo.getAllWithGeofences();


        for (WaypointModel w : loadedWaypoints) {
            Timber.d("id:%s, desc:%s, lat:%s, lon:%s, rad:%s", w.getId(), w.getDescription(), w.getGeofenceLatitude(), w.getGeofenceLongitude(), w.getGeofenceRadius());

            try {
                geofences.add(new Geofence.Builder()
                        .setRequestId(Long.toString(w.getId()))
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
// TODO: implement in lost myself?
//                        .setNotificationResponsiveness((int) TimeUnit.MINUTES.toMillis(2))
                        .setCircularRegion(w.getGeofenceLatitude(), w.getGeofenceLongitude(), w.getGeofenceRadius())
                        .setExpirationDuration(Geofence.NEVER_EXPIRE).build());
            } catch (IllegalArgumentException e) {
                Timber.e(e, "Invalid geofence parameter");
            }
        }

        if (geofences.size() > 0) {
            GeofencingRequest.Builder b = new GeofencingRequest.Builder();
            GeofencingRequest request = b.addGeofences(geofences).build();
            mGeofencingClient.addGeofences(lostApiClient, request, getGeofencePendingIntent());
        }
    }

    private boolean missingLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED;
    }

    private void removeGeofences() {
        if (lostApiClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Timber.e("removeGeofences failed due to no permission");
                // TODO: request permissions here
                return;
            }
            mGeofencingClient.removeGeofences(lostApiClient, getGeofencePendingIntent());
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(Events.WaypointAdded e) {
        locationProcessor.publishWaypointMessage(e.getWaypointModel()); // TODO: move to waypointsRepo
        if(e.getWaypointModel().hasGeofence()) {
            removeGeofences();
            setupGeofences();
            setupLocationRequest();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(Events.WaypointUpdated e) {
        locationProcessor.publishWaypointMessage(e.getWaypointModel()); // TODO: move to waypointsRepo
        removeGeofences();
        setupGeofences();
        setupLocationRequest();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(Events.WaypointRemoved e) {
        if(e.getWaypointModel().hasGeofence()) {
            removeGeofences();
            setupGeofences();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(Events.ModeChanged e) {
        removeGeofences();
        setupGeofences();
        setupLocationRequest();
        updateOngoingNotification();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(Events.MonitoringChanged e) {
        setupLocationRequest();
        updateOngoingNotification();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(MessageTransition message) {
        Timber.d("transition isIncoming:%s topic:%s", message.isIncoming(), message.getTopic());
        if (message.isIncoming())
            sendEventNotification(message);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(MessageLocation m) {
        Timber.d("MessageLocation received %s, %s, outgoing: %s", m, lastLocationMessage, !m.isIncoming());
        if (lastLocationMessage == null || lastLocationMessage.getTst() <= m.getTst()) {
            this.lastLocationMessage = m;
            updateOngoingNotification();
            geocodingProvider.resolve(m, this, true);
        }
    }

    public void onGeocodingProviderResult(MessageLocation m) {
        if (m == lastLocationMessage) {
            updateOngoingNotification();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void onEvent(MessageProcessor.EndpointState state) {
        Timber.d(state.getError(), "endpoint state changed %s. Message: %s", state.getLabel(this), state.getMessage());
        this.lastEndpointState = state;
        updateOngoingNotification();
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void onEvent(Events.QueueChanged e) {
        this.lastQueueLength = e.getNewLength();
        updateOngoingNotification();
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true)
    public void onEvent(Events.PermissionGranted event) {
        Timber.d("location permission granted");
        if (lostApiClient != null && mGeofencingClient != null) {
            removeGeofences();
            setupGeofences();
        }

        try {
            Timber.v("Getting last location");
            mFusedLocationClient.getLastLocation(lostApiClient);
        } catch (SecurityException ignored) {
        }

    }

    private NotificationCompat.Builder getEventsNotificationBuilder() {
        if (!preferences.getNotificationEvents())
            return null;

        Timber.d("building notification builder");

        if (eventsNotificationCompatBuilder != null)
            return eventsNotificationCompatBuilder;

        Timber.d("builder not present, lazy building");
        eventsNotificationCompatBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_EVENTS);

        Intent openIntent = new Intent(this, MapActivity.class);
        openIntent.setAction("android.intent.action.MAIN");
        openIntent.addCategory("android.intent.category.LAUNCHER");
        openIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        eventsNotificationCompatBuilder.setContentIntent(openPendingIntent);
        //eventsNotificationCompatBuilder.setDeleteIntent(ServiceProxy.getBroadcastIntentForService(this.context, ServiceProxy.SERVICE_NOTIFICATION, ServiceNotification.INTENT_ACTION_CANCEL_EVENT_NOTIFICATION, null));
        eventsNotificationCompatBuilder.setSmallIcon(R.drawable.ic_notification);
        eventsNotificationCompatBuilder.setAutoCancel(true);
        eventsNotificationCompatBuilder.setShowWhen(true);
        eventsNotificationCompatBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        eventsNotificationCompatBuilder.setCategory(NotificationCompat.CATEGORY_SERVICE);
        eventsNotificationCompatBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            eventsNotificationCompatBuilder.setColor(getColor(R.color.primary));
        }

        return eventsNotificationCompatBuilder;
    }

    @Override
    public void onConnected() {
        this.connected = true;
        setupLocationRequest();
        scheduler.scheduleLocationPing();
        setupGeofences();
    }

    @Override
    public void onConnectionSuspended() {
        this.connected = false;
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onAttachAfterModeChanged() {
        //NOOP. Handled through eventbus
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (preferences.getPreferenceKey(R.string.preferenceKeyLocatorInterval).equals(key) ||
                preferences.getPreferenceKey(R.string.preferenceKeyLocatorDisplacement).equals(key) ||
                preferences.getPreferenceKey(R.string.preferenceKeyLocatorPriority).equals(key) ||
                preferences.getPreferenceKey(R.string.preferenceKeyMoveModeLocatorInterval).equals(key)
        ) {
            Timber.d("locator preferences changed. Resetting location request.");
            setupLocationRequest();
        }
    }

    public class LocalBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Timber.v("in onBind()");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Timber.v("Last client unbound from service");
    }
}
