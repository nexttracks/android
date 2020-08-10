package org.nexttracks.android.ui.preferences.accounts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import org.nexttracks.android.R
import org.nexttracks.android.databinding.UiAccountsBinding
import org.nexttracks.android.ui.base.BaseActivity
import org.nexttracks.android.ui.base.view.MvvmView
import org.nexttracks.android.ui.preferences.account.AccountActivity

class AccountsActivity : BaseActivity<UiAccountsBinding, AccountsViewModel>(), MvvmView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasEventBus(false)
        bindAndAttachContentView(R.layout.ui_accounts, savedInstanceState)
        setSupportToolbar(binding.toolbar)
        setDrawer(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_accounts, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                navigator.startActivity(AccountActivity::class.java)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}