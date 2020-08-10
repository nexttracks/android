package org.nexttracks.android.injection.modules.android.ActivityModules

import androidx.appcompat.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import org.nexttracks.android.injection.scopes.PerActivity
import org.nexttracks.android.ui.preferences.accounts.AccountsActivity
import org.nexttracks.android.ui.preferences.accounts.AccountsMvvm
import org.nexttracks.android.ui.preferences.accounts.AccountsViewModel

@Module(includes = [BaseActivityModule::class])
abstract class AccountsActivityModule {
    @Binds
    @PerActivity
    abstract fun bindActivity(a: AccountsActivity): AppCompatActivity

    @Binds
    abstract fun bindViewModel(viewModel: AccountsViewModel): AccountsMvvm.ViewModel<AccountsMvvm.View>
}
