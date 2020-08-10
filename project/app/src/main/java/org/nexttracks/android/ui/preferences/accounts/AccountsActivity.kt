package org.nexttracks.android.ui.preferences.accounts

import android.os.Bundle
import android.view.Menu
import org.nexttracks.android.R
import org.nexttracks.android.databinding.UiAccountsBinding
import org.nexttracks.android.ui.base.BaseActivity
import org.nexttracks.android.ui.base.view.MvvmView

class AccountsActivity : BaseActivity<UiAccountsBinding, AccountsViewModel>(), MvvmView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasEventBus(false)
        bindAndAttachContentView(R.layout.ui_accounts, savedInstanceState)
        setSupportToolbar(binding.toolbar)
        setDrawer(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_accounts, menu)
        return true
    }
}