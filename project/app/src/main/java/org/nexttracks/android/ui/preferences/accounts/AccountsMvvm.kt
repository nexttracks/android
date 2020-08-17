package org.nexttracks.android.ui.preferences.accounts

import io.objectbox.query.Query
import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.data.WaypointModel
import org.nexttracks.android.ui.base.view.MvvmView
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel

interface AccountsMvvm {
    interface View : MvvmView

    interface ViewModel<V : MvvmView?> : MvvmViewModel<V> {
        val accountsList: Query<AccountModel>

        fun delete(model: AccountModel)
    }
}
