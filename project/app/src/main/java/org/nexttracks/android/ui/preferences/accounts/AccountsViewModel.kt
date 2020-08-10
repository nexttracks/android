package org.nexttracks.android.ui.preferences.accounts

import io.objectbox.query.Query
import org.greenrobot.eventbus.Subscribe
import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.data.WaypointModel
import org.nexttracks.android.data.repos.AccountsRepo
import org.nexttracks.android.injection.scopes.PerActivity
import org.nexttracks.android.support.Events
import org.nexttracks.android.support.Preferences
import org.nexttracks.android.ui.base.viewmodel.BaseViewModel
import javax.inject.Inject

@PerActivity
class AccountsViewModel @Inject constructor(private val accountsRepo: AccountsRepo) : BaseViewModel<AccountsMvvm.View>(), AccountsMvvm.ViewModel<AccountsMvvm.View> {
    override val accountsList: List<AccountModel>
        get() = this.accountsRepo.all


    override fun delete(model: AccountModel) {
        accountsRepo.delete(model);
    }
}