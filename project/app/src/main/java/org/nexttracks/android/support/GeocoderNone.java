package org.nexttracks.android.support;

public class GeocoderNone implements Geocoder {
    GeocoderNone() {
    }

    public String reverse(double latitude, double longitude, boolean brief) {
        return (latitude + " : " + longitude);
    }
}
