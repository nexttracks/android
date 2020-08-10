package org.nexttracks.android.data

import androidx.databinding.BaseObservable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique

@Entity
data class AccountModel(val username: String, val password: String, val hostname: String, val port: Short) : BaseObservable() {
    @Id var id: Long = 0
}