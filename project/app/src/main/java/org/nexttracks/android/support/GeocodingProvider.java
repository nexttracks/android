package org.nexttracks.android.support;

import android.content.Context;
import androidx.databinding.BindingAdapter;

import android.location.Address;
import android.os.AsyncTask;
import androidx.annotation.CallSuper;
import androidx.collection.LruCache;
import android.widget.TextView;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
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

    private static LruCache<String, String> cache;
    private static GeocoderNominatim geocoder;

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

    private static String getCache(MessageLocation m) {
        return cache.get(locationHash(m));
    }

    private static void putCache(MessageLocation m, String geocoder) {
        cache.put(locationHash(m), geocoder);
    }

    private static String locationHash(MessageLocation m) {
        return String.format(Locale.US,"%.6f-%.6f", m.getLatitude(), m.getLongitude());
    }

    private static boolean isCachedGeocoderAvailable(MessageLocation m) {
        String s = getCache(m);
        Timber.v("cache lookup for %s (hash %s) -> %s", m.getMessageId(), locationHash(m), getCache(m));

        if(s != null) {
            m.setGeocoder(s);
            return true;
        } else {
            return false;
        }
    }

    public static void resolve(MessageLocation m, TextView tv) {
        if(m.hasGeocoder()) {
            tv.setText(m.getGeocoder());
            return;
        }

        if(isCachedGeocoderAvailable(m)) {
            tv.setText(m.getGeocoder());
        } else {
            tv.setText(m.getGeocoderFallback()); // will print lat : lon until GeocodingProvider is available
            TextViewLocationResolverTask.run(m, tv);
        }
    }

    public void resolve(MessageLocation m, BackgroundService s) {
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
        protected void onPostExecute(String result) {
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

        static void run(MessageLocation m, TextView tv) {
            (new TextViewLocationResolverTask(m, tv)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        TextViewLocationResolverTask(MessageLocation m, TextView tv) {
            super(m);
            this.textView = new WeakReference<>(tv);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            TextView s = this.textView.get();
            if(s!=null && result != null) {
                s.setText(result);
            }
        }
    }

    private static abstract class MessageLocationResolverTask extends AsyncTask<Void, Void, String>  {
        final WeakReference<MessageLocation> message;
        MessageLocationResolverTask(MessageLocation m) {
            this.message = new WeakReference<>(m);
        }

        @Override
        protected String doInBackground(Void... params) {
            MessageLocation m = message.get();
            if(m == null) {
                return "Resolve failed";
            }

            String address = "Resolve failed";
            try {
                List<Address> addresses = geocoder.getFromLocation(m.getLatitude(), m.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    Address addr = addresses.get(0);
                    StringBuilder sb = new StringBuilder();
                    if (addr.getThoroughfare() != null) {
                        sb.append(addr.getThoroughfare());
                        if (addr.getSubThoroughfare() != null) {
                            sb.append(" ");
                            sb.append(addr.getSubThoroughfare());
                        }
                        sb.append(", ");
                    }
                    if (addr.getLocality() != null) {
                        if (addr.getPostalCode() != null) {
                            sb.append(addr.getPostalCode());
                        }
                        sb.append(" ");
                        sb.append(addr.getLocality());
                        sb.append(", ");
                    }
                    if (addr.getSubAdminArea() != null) {
                        sb.append(addr.getSubAdminArea());
                        sb.append(", ");
                    }
                    if (addr.getAdminArea() != null) {
                        sb.append(addr.getAdminArea());
                        sb.append(", ");
                    }
                    if (!sb.toString().isEmpty()) {
                        address = sb.toString();
                        if (address.endsWith(", ")) {
                            address = address.substring(0, address.length() - 2);
                        }
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            return address;
        }

        @Override
        @CallSuper
        protected void onPostExecute(String result) {
            MessageLocation m = message.get();
            Timber.v("geocoding result: %s", result);
            if(m != null && result != null) {
                m.setGeocoder(result);
                putCache(m, result);
            }
        }
    }

    @BindingAdapter({"android:text", "messageLocation"})
    public static void displayFusedLocationInViewAsync(TextView view, FusedContact c, MessageLocation m) {
        if(m != null)
            resolve(m, view);
        else
            view.setText(R.string.na);
    }

}
