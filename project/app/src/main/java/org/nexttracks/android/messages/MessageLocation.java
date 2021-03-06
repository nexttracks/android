package org.nexttracks.android.messages;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.nexttracks.android.support.GeocoderAddress;
import org.osmdroid.util.GeoPoint;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.support.interfaces.IncomingMessageProcessor;
import org.nexttracks.android.support.interfaces.OutgoingMessageProcessor;
import org.nexttracks.android.support.Preferences;

import java.lang.ref.WeakReference;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageLocation extends MessageBase {
    public static final String TYPE = "location";
    public static final String REPORT_TYPE_USER = "u";
    public static final String REPORT_TYPE_RESPONSE = "r";
    public static final String REPORT_TYPE_CIRCULAR = "c";
    public static final String REPORT_TYPE_PING = "p";
    public static final String REPORT_TYPE_DEFAULT = null;

    public static final String CONN_TYPE_OFFLINE = "o";
    public static final String CONN_TYPE_WIFI = "w";
    public static final String CONN_TYPE_MOBILE = "m";

    private String t;
    private int batt;
    private int acc;
    private int vac;
    private double lat;
    private double lon;
    private int alt;
    private int vel;
    private long tst;
    private GeocoderAddress geocoder;
    private WeakReference<FusedContact> _contact;
    private GeoPoint point;
    private String conn;
    private List<String> inregions;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty("_cp")
    private boolean cp = false;

    @JsonProperty("inregions")
    public List<String> getInRegions() {
        return inregions;
    }

    @JsonProperty("inregions")
    public void setInRegions(List<String> inregions) {
        this.inregions = inregions;
    }

    public boolean getCp() {
        return cp;
    }

    public void setCp(boolean cp) {
        this.cp = cp;
    }

    @JsonProperty("lat")
    public double getLatitude() {
        return lat;
    }

    @JsonProperty("lon")
    public double getLongitude() {
        return lon;
    }

    @JsonProperty("vel")
    public int getVelocity() {
        return vel;
    }

    public void setVelocity(int vel) {
        this.vel = vel;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public int getBatt() {
        return batt;
    }

    public void setBatt(int batt) {
        this.batt = batt;
    }

    public int getAcc() {
        return acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTst() {
        return tst;
    }

    public void setTst(long tst) {
        this.tst = tst;
    }

    @JsonIgnore
    public GeocoderAddress getGeocoder() {
        return hasGeocoder() ? geocoder : getGeocoderFallback();
    }

    @JsonIgnore
    public GeocoderAddress getGeocoderFallback() {
        return new GeocoderAddress(getLatitude() + " : " + getLongitude(), null);
    }

    @JsonIgnore
    public boolean hasGeocoder() {
        return geocoder != null;
    }

    @JsonIgnore
    public void setGeocoder(GeocoderAddress geocoder) {
        this.geocoder = geocoder;
        notifyContactPropertyChanged();
    }

    @JsonIgnore
    public String getBaseTopicSuffix() {
        return null;
    }

    public void setContact(FusedContact contact) {
        this._contact = new WeakReference<>(contact);
    }

    private void notifyContactPropertyChanged() {
        if (_contact != null && _contact.get() != null)
            this._contact.get().notifyMessageLocationPropertyChanged();

    }

    @JsonIgnore
    public GeoPoint getGeoPoint() {
        return point != null ? point : (point = new GeoPoint(lat, lon));
    }

    @Override
    public void processIncomingMessage(IncomingMessageProcessor handler) {
        handler.processIncomingMessage(this);
    }

    public void setTid(String tid) {
        super.setTid(tid);
        notifyContactPropertyChanged();
    }

    public boolean isValidMessage() {
        return tst > 0;
    }

    public void setConn(String conn) {
        this.conn = conn;
    }

    public String getConn() {
        return this.conn;
    }

    public void setAlt(int alt) {
        this.alt = alt;
    }

    public int getAlt() {
        return alt;
    }

    public void setVac(int vac) {
        this.vac = vac;
    }

    public int getVac() {
        return this.vac;
    }

    @JsonIgnore
    @Override
    @NonNull
    public String toString() {
        return String.format("%s: %s",super.toString(), this.getGeoPoint());
    }

    @Override
    public void addMqttPreferences(Preferences preferences) {
        setTopic(preferences.getPubTopicLocations());
        setQos(preferences.getPubQosLocations());
        setRetained(preferences.getPubRetainLocations());

    }
}

