package org.nexttracks.android.data.repos;

import org.nexttracks.android.messages.MessageCard;
import org.nexttracks.android.messages.MessageLocation;
import org.nexttracks.android.model.FusedContact;

import java.util.Collection;
import java.util.HashMap;

public interface ContactsRepo {
    HashMap<String, FusedContact> getAll();
    Collection<FusedContact> getAllAsList();

    FusedContact getById(String id);

    void clearAll();
    void remove(String id);

    void update(String id, MessageLocation m);
    void update(String id, MessageCard m);

    long getRevision();
}
