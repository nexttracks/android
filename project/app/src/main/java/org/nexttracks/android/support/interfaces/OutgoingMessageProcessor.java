package org.nexttracks.android.support.interfaces;

public interface OutgoingMessageProcessor {
    void onCreateFromProcessor();
    void onDestroy();

    void checkConfigurationComplete() throws ConfigurationIncompleteException;
}

