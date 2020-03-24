package org.nexttracks.android.support;

public class GeocoderNominatim implements Geocoder {
    GeocoderNominatim() {

    }

    public String reverse(double latitude, double longitude) {
        return (latitude + " : " + longitude);
    }

}
