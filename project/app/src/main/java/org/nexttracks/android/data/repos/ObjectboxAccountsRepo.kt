package org.nexttracks.android.data.repos

import android.content.Context
import io.objectbox.Box
import io.objectbox.query.Query
import org.greenrobot.eventbus.EventBus
import org.nexttracks.android.App
import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.data.AccountModel_
import org.nexttracks.android.data.WaypointModel_
import org.nexttracks.android.injection.qualifier.AppContext
import org.nexttracks.android.support.Preferences

class ObjectboxAccountsRepo(@AppContext context: Context, eventBus: EventBus, private val preferences: Preferences) : AccountsRepo(eventBus) {
    private val box: Box<AccountModel> = (App.getApplication() as App).boxStore.boxFor(AccountModel::class.java)
    override val all: Query<AccountModel>
        get() = box.query().order(AccountModel_.username).build()
    override val allList: List<AccountModel>
        get() = box.all

    override fun get(id: Long): AccountModel? {
        return box.query().equal(AccountModel_.id, id).build().findUnique()
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

}

