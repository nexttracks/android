package org.nexttracks.android.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.nexttracks.android.support.interfaces.IncomingMessageProcessor;
import org.nexttracks.android.support.interfaces.OutgoingMessageProcessor;
import org.nexttracks.android.support.Preferences;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageEvent extends MessageBase{
    static final String TYPE = "event";
    private static final String BASETOPIC_SUFFIX = "/event";
    public String getBaseTopicSuffix() {  return BASETOPIC_SUFFIX; }

    @Override
    public void addMqttPreferences(Preferences preferences) {
        setTopic(preferences.getPubTopicEvents());
    }

    @Override
    public void processIncomingMessage(IncomingMessageProcessor handler) {
        handler.processIncomingMessage(this);
    }
}
