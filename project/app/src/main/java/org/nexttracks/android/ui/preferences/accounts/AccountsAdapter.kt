package org.nexttracks.android.ui.preferences.accounts

import android.view.View
import io.objectbox.reactive.DataObserver
import org.nexttracks.android.BR
import org.nexttracks.android.R
import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.ui.base.BaseAdapter
import org.nexttracks.android.ui.base.BaseAdapterItemView

internal class AccountsAdapter(clickListener: ClickListener?) : BaseAdapter<AccountModel?>(BaseAdapterItemView.of(BR.account, R.layout.ui_row_account)), DataObserver<List<AccountModel>> {
    override fun onData(data: List<AccountModel>) {
        setItems(data)
    }

    internal interface ClickListener : BaseAdapter.ClickListener<AccountModel> {
        override fun onClick(model: AccountModel, view: View, longClick: Boolean)
    }

    init {
        setClickListener(clickListener)
    }
}