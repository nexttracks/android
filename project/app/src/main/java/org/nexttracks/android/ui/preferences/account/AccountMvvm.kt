package org.nexttracks.android.ui.preferences.account

import io.objectbox.query.Query
import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.ui.base.view.MvvmView
import org.nexttracks.android.ui.base.viewmodel.MvvmViewModel

interface AccountMvvm {
    interface View : MvvmView

    interface ViewModel<V : MvvmView?> : MvvmViewModel<V> {
        val accountsList: Query<AccountModel>
        fun loadAccount(accountId: Long)
        fun canSaveAccount(): Boolean
        fun saveAccount()
    }
}