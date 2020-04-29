package org.nexttracks.android.ui.contacts;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.nexttracks.android.data.repos.ContactsRepo;
import org.nexttracks.android.injection.qualifier.AppContext;
import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.support.Events;
import org.nexttracks.android.ui.base.viewmodel.BaseViewModel;
import org.nexttracks.android.ui.map.MapActivity;

import java.util.Collection;

import javax.inject.Inject;


@PerActivity
public class ContactsViewModel extends BaseViewModel<ContactsMvvm.View> implements ContactsMvvm.ViewModel<ContactsMvvm.View> {

    private final ContactsRepo contactsRepo;

    @Inject
    public ContactsViewModel(@AppContext Context context, ContactsRepo contactsRepo) {
        this.contactsRepo = contactsRepo;
    }

    public void attachView(@NonNull ContactsMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
    }

    @Override
    public Collection<FusedContact> getContacts() {
        return contactsRepo.getAllAsList();
    }

    @Override
    public void onContactClick(FusedContact c) {
        if (!c.hasLocation())
            return;

        Bundle b = new Bundle();
        b.putString(MapActivity.BUNDLE_KEY_CONTACT_ID, c.getId());
        navigator.startActivity(MapActivity.class, b);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.FusedContactAdded c) {
        //TODO: insert, sort
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.FusedContactRemoved c) {
        //TODO: remove
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FusedContact c) {
        //TODO: Sort
    }

}
