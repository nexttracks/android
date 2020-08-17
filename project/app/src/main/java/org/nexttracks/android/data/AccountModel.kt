package org.nexttracks.android.data

import androidx.databinding.BaseObservable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique
import org.nexttracks.android.R

@Entity
data class AccountModel(@Id var id: Long, var username: String, var password: String, var hostname: String, var port: Int) : BaseObservable() {
    constructor(username: String, password: String, hostname: String, port: Int) : this(0, username, password, hostname, port)
    constructor() : this("", "", "", 8883)
}