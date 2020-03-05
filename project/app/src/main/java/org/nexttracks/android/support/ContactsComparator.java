package org.nexttracks.android.support;

import org.nexttracks.android.model.FusedContact;

import java.util.Comparator;


class ContactsComparator implements Comparator<FusedContact> {
    @Override
    public int compare(FusedContact o1, FusedContact o2) {
        return o1.compareTo(o2);
    }
}