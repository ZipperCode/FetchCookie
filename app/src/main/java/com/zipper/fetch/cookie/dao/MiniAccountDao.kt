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

    @Query("SELECT * FROM mini_account")
    fun allUsers(): Flow<List<MiniAccount>>
}