package org.nexttracks.android.support;

import android.location.Address;

import java.io.IOException;
import java.util.List;

public class GeocoderNominatim implements Geocoder {
    private static final long minimumDelay = 1000;

    private org.osmdroid.bonuspack.location.GeocoderNominatim geocoder;
    private long lastReverseTime = 0;

    GeocoderNominatim(String userAgent) {
        this.geocoder = new org.osmdroid.bonuspack.location.GeocoderNominatim(userAgent);
    }

    public synchronized GeocoderAddress reverse(double latitude, double longitude) {
        String primary = "Resolve failed";
        String secondary = null;
        try {
            long timeDiff = System.currentTimeMillis() - this.lastReverseTime;
            if (timeDiff < minimumDelay) {
                Thread.sleep(minimumDelay - timeDiff);
            }

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
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
                        sb.append(" ");
                    }
                    sb.append(addr.getLocality());
                    sb.append(", ");
                }
                StringBuilder sb2 = new StringBuilder();
                if (addr.getSubAdminArea() != null) {
                    sb2.append(addr.getSubAdminArea());
                    sb2.append(", ");
                }
                if (addr.getAdminArea() != null) {
                    sb2.append(addr.getAdminArea());
                }
                if (!sb.toString().isEmpty()) {
                    primary = sb.toString();
                    if (primary.endsWith(", ")) {
                        primary = primary.substring(0, primary.length() - 2);
                    }
                }
                if (!sb2.toString().isEmpty()) {
                    secondary = sb2.toString();
                    if (secondary.endsWith(", ")) {
                        secondary = secondary.substring(0, secondary.length() - 2);
                    }
                }
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
        this.lastReverseTime = System.currentTimeMillis();
        return new GeocoderAddress(primary, secondary);
    }
}
