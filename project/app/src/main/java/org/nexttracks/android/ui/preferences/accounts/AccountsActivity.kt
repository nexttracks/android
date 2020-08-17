package org.nexttracks.android.ui.preferences.accounts

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import io.objectbox.android.AndroidScheduler
import io.objectbox.reactive.DataSubscription
import org.nexttracks.android.R
import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.databinding.UiAccountsBinding
import org.nexttracks.android.ui.base.BaseActivity
import org.nexttracks.android.ui.base.view.MvvmView
import org.nexttracks.android.ui.preferences.account.AccountActivity
import org.nexttracks.android.ui.region.RegionActivity
import timber.log.Timber

class AccountsActivity : BaseActivity<UiAccountsBinding, AccountsViewModel>(), MvvmView, AccountsAdapter.ClickListener {

    private lateinit var recyclerViewAdapter: AccountsAdapter
    private var subscription: DataSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasEventBus(false)
        bindAndAttachContentView(R.layout.ui_accounts, savedInstanceState)
        setSupportToolbar(binding.toolbar)
        setDrawer(binding.toolbar)

        recyclerViewAdapter = AccountsAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = recyclerViewAdapter
        binding.recyclerView.setEmptyView(binding.placeholder)
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

    override fun onStart() {
        super.onStart()
        if (this.subscription == null || this.subscription!!.isCanceled) {
            this.subscription = viewModel.accountsList.subscribe().on(AndroidScheduler.mainThread()).observer(recyclerViewAdapter)
        }
    }

    override fun onClick(model: AccountModel, view: View, longClick: Boolean) {
        if (longClick) {
            Timber.v("model %s ", model.username)

            //TODO: Refactor and make nicer
            val myQuittingDialogBox = AlertDialog.Builder(this) //set message, title, and icon
                    .setTitle("Delete")
                    .setMessage("Do you want to Delete")
                    .setPositiveButton("Delete") { dialog: DialogInterface, _: Int ->
                        viewModel.delete(model)
                        dialog.dismiss()
                    }.setNegativeButton("cancel") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                    .create()
            myQuittingDialogBox.show()
        } else {
            val b = Bundle()
            b.putLong("accountId", model.id)
            navigator.startActivity(AccountActivity::class.java, b)
        }
    }
}