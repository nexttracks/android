package org.nexttracks.android.data.repos

import io.objectbox.query.Query
import org.greenrobot.eventbus.EventBus
import org.nexttracks.android.data.AccountModel
import org.nexttracks.android.messages.MessageAccount
import org.nexttracks.android.support.Events.*
import org.nexttracks.android.support.MessageAccountCollection

abstract class AccountsRepo protected constructor(private val eventBus: EventBus) {
    abstract operator fun get(id: Long): AccountModel?
    abstract val all: Query<AccountModel>
    abstract val allList: List<AccountModel>

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

    open fun importFromMessage(accounts: MessageAccountCollection) {
        for (m in accounts) {
            // Delete existing account if one with the same tst already exists
            val existing: AccountModel? = get(m.id)
            existing?.let { delete(it) }
            insert(toDaoObject(m))
        }
    }

    open fun exportToMessage(): MessageAccountCollection {
        val messages = MessageAccountCollection()
        for (account in allList) {
            messages.add(fromDaoObject(account))
        }
        return messages
    }

    open fun toDaoObject(messageAccount: MessageAccount): AccountModel {
        return AccountModel(messageAccount.id, messageAccount.username, messageAccount.password, messageAccount.hostname, messageAccount.port, messageAccount.enabled)
    }

    open fun fromDaoObject(a: AccountModel): MessageAccount {
        return MessageAccount(a.id, a.username, a.password, a.hostname, a.port, a.enabled)
    }
}