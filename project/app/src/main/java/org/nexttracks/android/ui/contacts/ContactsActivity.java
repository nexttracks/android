package org.nexttracks.android.ui.contacts;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import org.nexttracks.android.R;
import org.nexttracks.android.databinding.UiContactsBinding;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.ui.base.BaseActivity;

import java.util.Collections;


public class ContactsActivity extends BaseActivity<UiContactsBinding, ContactsMvvm.ViewModel> implements ContactsMvvm.View, org.nexttracks.android.ui.contacts.ContactsAdapter.ClickListener {
    private ObservableList<FusedContact> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mList = new ObservableArrayList<>();
        bindAndAttachContentView(R.layout.ui_contacts, savedInstanceState);
        setSupportToolbar(binding.toolbar);
        setDrawer(binding.toolbar);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new ContactsAdapter(mList, this));
    }

    @Override
    public void onClick(@NonNull FusedContact object, @NonNull View view, boolean longClick) {
        viewModel.onContactClick(object);
    }
    @Override
    public void onResume() {
        super.onResume();
        mList.clear();
        mList.addAll(viewModel.getContacts());
    }

    @Override
    public void removeContact(FusedContact c) {
        mList.remove(c);
    }

    @Override
    @MainThread
    public void addContact(FusedContact c) {
        mList.add(c);
        Collections.sort(mList);
    }

    @Override
    @MainThread
    public void updateContact(FusedContact c) {
        Collections.sort(mList);
    }
}
