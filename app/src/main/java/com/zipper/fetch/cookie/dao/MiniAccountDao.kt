package com.zipper.fetch.cookie.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 *
 * @author  zhangzhipeng
 * @date    2023/8/21
 */
@Dao
interface MiniAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: MiniAccount)

    @Update
    suspend fun update(account: MiniAccount)

    @Query("SELECT count(*) FROM mini_account")
    suspend fun count(): Long

    @Query("SELECT * FROM mini_account WHERE type = :type and phone = :phone")
    suspend fun get(type: Int, phone: String): MiniAccount?

    @Query("SELECT * FROM mini_account")
    fun allUsers(): Flow<List<MiniAccount>>

    @Query("SELECT * FROM mini_account")
    suspend fun getAllUsers(): List<MiniAccount>
}