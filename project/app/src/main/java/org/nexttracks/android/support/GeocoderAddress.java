package org.nexttracks.android.support;

public class GeocoderAddress {
    public String primary;
    public String secondary;

    public GeocoderAddress(String primary, String secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public String convert(boolean brief) {
        return (brief || this.secondary == null) ? primary : (primary + ", " + this.secondary);
    }
}
