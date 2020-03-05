package org.nexttracks.android.services;

import org.nexttracks.android.messages.MessageBase;
import org.nexttracks.android.support.interfaces.OutgoingMessageProcessor;

public abstract class MessageProcessorEndpoint implements OutgoingMessageProcessor {
    MessageProcessor messageProcessor;

    MessageProcessorEndpoint(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    void onMessageReceived(MessageBase message) {
        message.setModeId(getModeId());
        messageProcessor.onMessageReceived(onFinalizeMessage(message));
    }

    protected abstract MessageBase onFinalizeMessage(MessageBase message);
    abstract int getModeId();
}
