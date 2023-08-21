package com.zipper.fetch.cookie.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zipper.fetch.cookie.App

/**
 *
 * @author zhangzhipeng
 * @date 2023/8/21
 */
@Database(entities = [MiniAccount::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getMiniAccountDao(): MiniAccountDao

    companion object {

        val current: AppDataBase by lazy {
            Room.databaseBuilder(App.appContext, AppDataBase::class.java, "fetch_cookie")
                .build()
        }
    }
}
