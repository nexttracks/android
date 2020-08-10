package org.nexttracks.android.data.repos

import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.query.Query
import org.greenrobot.eventbus.EventBus
import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.support.Events.*

abstract class AccountsRepo protected constructor(private val eventBus: EventBus) {
    abstract operator fun get(id: Long): AccountModel
    abstract val all: List<AccountModel>

    fun insert(w: AccountModel) {
        insertImpl(w)
        eventBus.post(AccountAdded(w))
    }

    fun update(w: AccountModel, notify: Boolean) {
        updateImpl(w)
        if (notify) {
            eventBus.post(AccountUpdated(w))
        }
    }

    fun delete(w: AccountModel) {
        deleteImpl(w)
        eventBus.post(AccountRemoved(w))
    }

    protected abstract fun insertImpl(w: AccountModel)
    protected abstract fun updateImpl(w: AccountModel)
    protected abstract fun deleteImpl(w: AccountModel)

}