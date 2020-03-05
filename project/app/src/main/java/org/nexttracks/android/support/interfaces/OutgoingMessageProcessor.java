package org.nexttracks.android.support.interfaces;


import org.nexttracks.android.messages.MessageBase;
import org.nexttracks.android.messages.MessageClear;
import org.nexttracks.android.messages.MessageCmd;
import org.nexttracks.android.messages.MessageEvent;
import org.nexttracks.android.messages.MessageLocation;
import org.nexttracks.android.messages.MessageTransition;
import org.nexttracks.android.messages.MessageWaypoint;
import org.nexttracks.android.messages.MessageWaypoints;

public interface OutgoingMessageProcessor {
    void processOutgoingMessage(MessageBase message);
    void processOutgoingMessage(MessageCmd message);
    void processOutgoingMessage(MessageEvent message);
    void processOutgoingMessage(MessageLocation message);
    void processOutgoingMessage(MessageTransition message);
    void processOutgoingMessage(MessageWaypoint message);
    void processOutgoingMessage(MessageWaypoints message);
    void processOutgoingMessage(MessageClear message);

    void onCreateFromProcessor();
    void onDestroy();

    boolean isConfigurationComplete();
}
