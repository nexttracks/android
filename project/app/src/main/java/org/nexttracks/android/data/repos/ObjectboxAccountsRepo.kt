package org.nexttracks.android.data.repos

import android.content.Context
import io.objectbox.Box
import org.greenrobot.eventbus.EventBus
import org.nexttracks.android.App
import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.data.AccountModel_
import org.nexttracks.android.data.MyObjectBox
import org.nexttracks.android.injection.qualifier.AppContext
import org.nexttracks.android.support.Preferences

class ObjectboxAccountsRepo(@AppContext context: Context, eventBus: EventBus, preferences: Preferences) : AccountsRepo(eventBus) {
    private val preferences: Preferences
    private val box: Box<AccountModel>
    override val all: List<AccountModel>
        get() = box.all

    override fun get(id: Long): AccountModel {
        return box.query().equal(AccountModel_.id, id).build().findUnique()!!
    }

    override fun insertImpl(w: AccountModel) {
        box.put(w)
    }

    override fun updateImpl(w: AccountModel) {
        box.put(w)
    }

    override fun deleteImpl(w: AccountModel) {
        box.remove(w)
    }

    init {
        box = (App.getApplication() as App).boxStore.boxFor(AccountModel::class.java)
        this.preferences = preferences
    }
}

