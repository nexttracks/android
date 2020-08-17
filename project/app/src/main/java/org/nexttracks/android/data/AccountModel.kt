package org.nexttracks.android.data

import androidx.databinding.BaseObservable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique

@Entity
data class AccountModel(var username: String, var password: String, var hostname: String, var port: Int) : BaseObservable() {
    @Id var id: Long = 0
    constructor() : this("", "", "", -1)
}