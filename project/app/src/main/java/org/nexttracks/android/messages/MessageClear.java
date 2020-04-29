package org.nexttracks.android.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.nexttracks.android.support.interfaces.IncomingMessageProcessor;
import org.nexttracks.android.support.interfaces.OutgoingMessageProcessor;
import org.nexttracks.android.support.Preferences;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type")
public class MessageClear extends MessageBase {
    static final String TYPE = "clear";

    @Override
    public void processIncomingMessage(IncomingMessageProcessor handler) {
        handler.processIncomingMessage(this);
    }

    @Override
    @JsonIgnore
    public String getBaseTopicSuffix() {
        return null;
    }

    @JsonIgnore
    private String infoTopic;

    public String getInfoTopic() {
        return infoTopic;
    }

    @Override
    public void addMqttPreferences(Preferences preferences) {
        setRetained(true);
        setTopic(preferences.getPubTopicBase());
        infoTopic = this._topic+ preferences.getPubTopicInfoPart();
    }


}
