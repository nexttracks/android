package org.nexttracks.android.messages;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.nexttracks.android.support.interfaces.IncomingMessageProcessor;
import org.nexttracks.android.support.MessageWaypointCollection;
import org.nexttracks.android.support.interfaces.OutgoingMessageProcessor;
import org.nexttracks.android.support.Preferences;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type")
public class MessageConfiguration extends MessageBase{
    static final String TYPE = "configuration";
    private final Map<String,Object> map = new TreeMap<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MessageWaypointCollection waypoints;

    @Override
    public void processIncomingMessage(IncomingMessageProcessor handler) {
        handler.processIncomingMessage(this);
    }

    public MessageWaypointCollection getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(MessageWaypointCollection waypoints) {
        this.waypoints = waypoints;
    }

    @JsonAnyGetter
    @JsonPropertyOrder(alphabetic=true)
    public Map<String,Object> any() {
        return map;
    }

    @JsonAnySetter
    public void set(String key, Object value) {
        if(value instanceof String && "".equals(value))
            return;
        map.put(key, value);
    }

    // TID would not be included in map for load otherwise
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setTid(String tid) {
        set("tid", tid);
    }

    @Override
    public void addMqttPreferences(Preferences preferences) {

    }

    @JsonIgnore
    public Object get(String key) {
        return map.get(key);
    }

    @JsonIgnore
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    @JsonIgnore
    public String getBaseTopicSuffix() {
        return null;
    }

    @JsonIgnore
    public Set<String> getKeys() {
        return map.keySet();
    }

    @JsonIgnore
    public void removeKey(String key) {
        map.remove(key);
    }

    @JsonIgnore
    public boolean hasWaypoints() {
        return waypoints != null && waypoints.size() > 0;
    }

}
