package org.nexttracks.android.services;

import org.nexttracks.android.messages.MessageBase;
import org.nexttracks.android.support.interfaces.OutgoingMessageProcessor;
import org.nexttracks.android.support.interfaces.ConfigurationIncompleteException;

import java.io.IOException;

public abstract class MessageProcessorEndpoint implements OutgoingMessageProcessor {
    MessageProcessor messageProcessor;

    MessageProcessorEndpoint(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    void onMessageReceived(MessageBase message) {
        message.setIncoming();
        message.setModeId(getModeId());
        onFinalizeMessage(message).processIncomingMessage(messageProcessor);
    }

    protected abstract MessageBase onFinalizeMessage(MessageBase message);

    abstract int getModeId();

    abstract void sendMessage(MessageBase m) throws ConfigurationIncompleteException, OutgoingMessageSendingException, IOException;
}

class OutgoingMessageSendingException extends Exception {
    OutgoingMessageSendingException(Exception e) {
        super(e);
    }
}
