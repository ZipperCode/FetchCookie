package com.zipper.fetch.cookie.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mini_account")
data class MiniAccount(
    val phone: String,
    val token: String,
    val type: Int,
    val isExpired: Boolean = true,
    val lastAppointTime: Long = 0,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int? = null
}
