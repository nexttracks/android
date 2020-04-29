package org.nexttracks.android.support.interfaces;


public interface StatefulServiceMessageProcessor {
    void reconnect();
    void disconnect();
    boolean checkConnection();
}
