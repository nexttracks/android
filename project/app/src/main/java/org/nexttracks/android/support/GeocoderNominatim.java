package org.nexttracks.android.support;

import android.location.Address;

import java.io.IOException;
import java.util.List;

public class GeocoderNominatim implements Geocoder {
    private org.osmdroid.bonuspack.location.GeocoderNominatim geocoder;

    GeocoderNominatim(String userAgent) {
        this.geocoder = new org.osmdroid.bonuspack.location.GeocoderNominatim(userAgent);
    }

    public String reverse(double latitude, double longitude) {
        String address = "Resolve failed";
        try {
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
}
