package org.nexttracks.android.ui.contacts;

import androidx.databinding.ObservableList;
import androidx.annotation.NonNull;
import android.view.View;

import org.nexttracks.android.BR;
import org.nexttracks.android.R;
import org.nexttracks.android.model.FusedContact;
import org.nexttracks.android.ui.base.BaseAdapter;
import org.nexttracks.android.ui.base.BaseAdapterItemView;


class ContactsAdapter extends BaseAdapter<FusedContact> {
    ContactsAdapter(ObservableList items, ClickListener clickListener) {
        super(BaseAdapterItemView.of(BR.contact, R.layout.ui_row_contact));
        setItems(items);
        setClickListener(clickListener);
    }

    interface ClickListener extends BaseAdapter.ClickListener<FusedContact> {
        void onClick(@NonNull FusedContact object , @NonNull View view, boolean longClick);
    }
}
