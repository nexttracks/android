package org.nexttracks.android.injection.modules.android.ActivityModules

import androidx.appcompat.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import org.nexttracks.android.injection.scopes.PerActivity
import org.nexttracks.android.ui.preferences.account.AccountActivity
import org.nexttracks.android.ui.preferences.account.AccountMvvm
import org.nexttracks.android.ui.preferences.account.AccountViewModel

@Module(includes = [BaseActivityModule::class])
abstract class AccountActivityModule {
    @Binds
    @PerActivity
    abstract fun bindActivity(a: AccountActivity): AppCompatActivity

    @Binds
    abstract fun bindViewModel(viewModel: AccountViewModel): AccountMvvm.ViewModel<AccountMvvm.View>
}
