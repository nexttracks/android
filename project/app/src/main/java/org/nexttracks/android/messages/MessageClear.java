package org.nexttracks.android.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.nexttracks.android.support.Parser;
import org.nexttracks.android.support.Preferences;
import org.nexttracks.android.support.interfaces.IncomingMessageProcessor;

public class MessageClear extends MessageBase {
    @Override
    public void processIncomingMessage(IncomingMessageProcessor handler) {
        handler.processIncomingMessage(this);
    }

    @Override
    @JsonIgnore
    public String getBaseTopicSuffix() {
        return null;
    }

    @Override
    public void addMqttPreferences(Preferences preferences) {
        setRetained(true);
        setTopic(preferences.getPubTopicBase());
    }

    // Clear messages are implemented as empty messages
    @Override
    public byte[] toJsonBytes(Parser parser) {
        return new byte[0];
    }
    @Override
    public String toJson(Parser parser) {
        return "";
    }
}
