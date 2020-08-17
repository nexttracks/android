package org.nexttracks.android.data

import androidx.databinding.BaseObservable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique
import org.nexttracks.android.R

@Entity
data class AccountModel(@Id var id: Long, var username: String, var password: String, var hostname: String, var port: Int, var enabled: Boolean) : BaseObservable() {
    constructor(username: String, password: String, hostname: String, port: Int, enabled: Boolean) : this(0, username, password, hostname, port, enabled)
    constructor() : this("", "", "", 8883, true)
}