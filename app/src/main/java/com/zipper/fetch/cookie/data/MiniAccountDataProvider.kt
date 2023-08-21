package com.zipper.fetch.cookie.data

import com.zipper.fetch.cookie.dao.MiniAccount
import kotlin.random.Random

object MiniAccountDataProvider {

    fun getAccountList() : List<MiniAccount> {
        val random = Random(System.currentTimeMillis())
        val dataList = mutableListOf<MiniAccount>()
        for(i in 0 until 10) {
            dataList.add(MiniAccount("" + random.nextLong(), "", 0))
        }
        return dataList
    }

}