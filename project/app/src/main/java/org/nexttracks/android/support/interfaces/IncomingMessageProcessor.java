package org.nexttracks.android.support.interfaces;

import org.nexttracks.android.messages.MessageBase;
import org.nexttracks.android.messages.MessageCard;
import org.nexttracks.android.messages.MessageClear;
import org.nexttracks.android.messages.MessageCmd;
import org.nexttracks.android.messages.MessageLocation;
import org.nexttracks.android.messages.MessageTransition;
import org.nexttracks.android.messages.MessageUnknown;

public interface IncomingMessageProcessor {
    void processIncomingMessage(MessageBase message);
    void processIncomingMessage(MessageLocation message);
    void processIncomingMessage(MessageCard message);
    void processIncomingMessage(MessageCmd message);
    void processIncomingMessage(MessageTransition message);
    void processIncomingMessage(MessageUnknown message);
    void processIncomingMessage(MessageClear message);

}
