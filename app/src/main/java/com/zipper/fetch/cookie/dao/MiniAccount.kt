package com.zipper.fetch.cookie.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zipper.fetch.cookie.ui.minimt.model.EMPTY_TOKEN_DATA
import com.zipper.fetch.cookie.ui.minimt.model.MiniTokenData

@Entity(tableName = "mini_account")
data class MiniAccount constructor(
    val phone: String,
    val token: String,
    val type: Int,
    val isExpired: Boolean = false,
    val lastAppointTime: Long = 0,
    val lastOperationTime: Long = 0,
    var userInfo: MiniTokenData? = EMPTY_TOKEN_DATA
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id: Int? = null
}
