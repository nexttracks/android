package org.nexttracks.android.support;

import android.content.Context;
import androidx.databinding.BindingAdapter;

import android.location.Address;
import android.os.AsyncTask;
import androidx.annotation.CallSuper;
import androidx.collection.LruCache;
import android.widget.TextView;

import org.nexttracks.android.R;
import org.nexttracks.android.injection.qualifier.AppContext;
import org.nexttracks.android.injection.scopes.PerApplication;
import org.nexttracks.android.messages.MessageLocation;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.services.BackgroundService;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

@PerApplication
public class GeocodingProvider {

    private static LruCache<String, GeocoderAddress> cache;
    private static Geocoder geocoder;

    @Inject
    public GeocodingProvider(@AppContext Context context, Preferences preferences) {
        cache = new LruCache<>(40);
        if(preferences.getGeocodeEnabled()) {
            geocoder = new GeocoderNominatim("NextTracks App");
        }
        else {
            geocoder = new GeocoderNone();
        }
    }

    private static GeocoderAddress getCache(MessageLocation m) {
        return cache.get(locationHash(m));
    }

    private static void putCache(MessageLocation m, GeocoderAddress geocoder) {
        if (!geocoder.primary.equals("Resolve failed")) {
            cache.put(locationHash(m), geocoder);
        }
    }

    private static String locationHash(MessageLocation m) {
        return String.format(Locale.US,"%.6f-%.6f", m.getLatitude(), m.getLongitude());
    }

    private static boolean isCachedGeocoderAvailable(MessageLocation m) {
        GeocoderAddress ga = getCache(m);
        Timber.v("cache lookup for %s (hash %s) -> %s", m.getMessageId(), locationHash(m), getCache(m));

        if(ga != null) {
            m.setGeocoder(ga);
            return true;
        } else {
            return false;
        }
    }

    public static void resolve(MessageLocation m, TextView tv, boolean brief) {
        if(m.hasGeocoder()) {
            tv.setText(m.getGeocoder().convert(brief));
            return;
        }

        if(isCachedGeocoderAvailable(m)) {
            tv.setText(m.getGeocoder().convert(brief));
        } else {
            tv.setText(m.getGeocoderFallback().convert(brief)); // will print lat : lon until GeocodingProvider is available
            TextViewLocationResolverTask.run(m, tv, brief);
        }
    }

    public void resolve(MessageLocation m, BackgroundService s, boolean brief) {
        if(m.hasGeocoder()) {
            s.onGeocodingProviderResult(m);
            return;
        }

        if(isCachedGeocoderAvailable(m)) {
            s.onGeocodingProviderResult(m);
        } else {
            NotificationLocationResolverTask.run(m, s);
        }
    }

    private static class NotificationLocationResolverTask extends MessageLocationResolverTask {

        private final WeakReference<BackgroundService> service;

        static void run(MessageLocation m, BackgroundService s) {
            (new NotificationLocationResolverTask(m, s)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        NotificationLocationResolverTask(MessageLocation m, BackgroundService service) {
            super(m);
            this.service = new WeakReference<>(service);
        }

        @Override
        protected void onPostExecute(GeocoderAddress result) {
            super.onPostExecute(result);
            MessageLocation m = this.message.get();
            BackgroundService s = this.service.get();
            if(m!=null && s!=null) {
                s.onGeocodingProviderResult(m);
            }
        }
    }

    private static class TextViewLocationResolverTask extends MessageLocationResolverTask {

        private final WeakReference<TextView> textView;
        private final boolean brief;

        static void run(MessageLocation m, TextView tv, boolean brief) {
            (new TextViewLocationResolverTask(m, tv, brief)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        TextViewLocationResolverTask(MessageLocation m, TextView tv, boolean brief) {
            super(m);
            this.textView = new WeakReference<>(tv);
            this.brief = brief;
        }

        @Override
        protected void onPostExecute(GeocoderAddress result) {
            super.onPostExecute(result);

            TextView s = this.textView.get();
            if(s != null) {
                s.setText(result.convert(this.brief));
            }
        }
    }

    private static abstract class MessageLocationResolverTask extends AsyncTask<Void, Void, GeocoderAddress>  {
        final WeakReference<MessageLocation> message;

        MessageLocationResolverTask(MessageLocation m) {
            this.message = new WeakReference<>(m);
        }

        @Override
        protected GeocoderAddress doInBackground(Void... params) {
            MessageLocation m = message.get();
            if(m == null) {
                return new GeocoderAddress("Resolve failed", null);
            }

            return geocoder.reverse(m.getLatitude(), m.getLongitude());
        }

        @Override
        @CallSuper
        protected void onPostExecute(GeocoderAddress result) {
            MessageLocation m = message.get();
            Timber.v("geocoding result: { primary: %s; secondary: %s }", result.primary, result.secondary);
            if(m != null && result != null) {
                m.setGeocoder(result);
                putCache(m, result);
            }
        }
    }

    @BindingAdapter({"android:text", "messageLocation"})
    public static void displayFusedLocationInViewAsync(TextView view, FusedContact c, MessageLocation m) {
        if(m != null)
            resolve(m, view, true);
        else
            view.setText(R.string.na);
    }

}
