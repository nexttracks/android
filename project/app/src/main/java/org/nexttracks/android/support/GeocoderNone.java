package org.nexttracks.android.support;

public class GeocoderNone implements Geocoder {
    GeocoderNone() {
    }

    public GeocoderAddress reverse(double latitude, double longitude) {
        return new GeocoderAddress(latitude + " : " + longitude, null);
    }
}

