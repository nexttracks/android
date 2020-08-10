package org.nexttracks.android.ui.preferences.account

import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.data.repos.AccountsRepo
import org.nexttracks.android.injection.scopes.PerActivity
import org.nexttracks.android.ui.base.viewmodel.BaseViewModel
import javax.inject.Inject

@PerActivity
class AccountViewModel @Inject constructor(private val accountsRepo: AccountsRepo) : BaseViewModel<AccountMvvm.View>(), AccountMvvm.ViewModel<AccountMvvm.View> {
    override val accountsList: List<AccountModel>
        get() = this.accountsRepo.all

    var account: AccountModel = AccountModel("", "", "", -1)

    var portText: String?
        get() = if (account.port >= 0) {
            account.port.toString()
        } else {
            null
        }
        set(string) {
            if (!string.isNullOrEmpty()) {
                account.port = string.toInt()
            }
        }

    override fun canSaveAccount(): Boolean {
        return account.username.isNotEmpty() && account.password.isNotEmpty() &&
                account.hostname.isNotEmpty() && account.port >= 0 && account.port <= 65535
    }

    override fun saveAccount() {
        accountsRepo.insert(account);
    }
}