package org.nexttracks.android.ui.preferences.account

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import org.nexttracks.android.R
import org.nexttracks.android.databinding.UiAccountBinding
import org.nexttracks.android.databinding.UiAccountsBinding
import org.nexttracks.android.ui.base.BaseActivity
import org.nexttracks.android.ui.base.view.MvvmView
import org.nexttracks.android.ui.preferences.accounts.AccountsActivity

class AccountActivity : BaseActivity<UiAccountBinding, AccountViewModel>(), MvvmView, TextWatcher {

    lateinit var saveButton: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasEventBus(false)
        bindAndAttachContentView(R.layout.ui_account, savedInstanceState)
        setSupportToolbar(binding.toolbar)
        setDrawer(binding.toolbar)
        binding.username.addTextChangedListener(this)
        binding.password.addTextChangedListener(this)
        binding.hostname.addTextChangedListener(this)
        binding.port.addTextChangedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_account, menu)
        this.saveButton = menu.findItem(R.id.save)
        conditionallyEnableSaveButton()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                viewModel.saveAccount()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun afterTextChanged(s: Editable?) {
        conditionallyEnableSaveButton()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    private fun conditionallyEnableSaveButton() {
        saveButton.isEnabled = viewModel.canSaveAccount()
        saveButton.icon.alpha = if (viewModel.canSaveAccount()) 255 else 130
    }
}