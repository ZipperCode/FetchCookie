package com.zipper.fetch.maotai.zhcs

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val a = mapOf(
            "X-HMAC-SIGNATURE" to "bEdK1CHXAf/rfr0tCpxx9R70AP9eg/0DFwTPM39gWdo=",
            "X-HMAC-ACCESS-KEY" to "dceec997f6c9c222ac122f727ec42668",
            "X-HMAC-ALGORITHM" to "hmac-sha256",
            "X-HMAC-DIGEST" to "OkLlfzKdT95XKYUcDbybptHnZDJ++Wo+PMFt4pkD+Po=",
            "X-HMAC-Date" to "Sat, 19 Aug 2023 03:46:20 GMT"
        )

        println("origin = $a")
        val data = Crypto.encryptionData("POST",
            "/front-manager/api/customer/queryById/token",
            "{\"channel\":\"h5\"}", 0,
            "39414a3d423249ffb2fec95915fd9ac6",
            "634143d4f5b08349fa83d92366e19fc1")
        println(data)
        assertEquals(4, 2 + 2)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun queryByToken() {
        runBlocking {
            println("start")
            Zhcs.getInfo()
            Zhcs.queryByIdToken()
            println("end")
        }
    }
    @Test
    fun sendCode() {
        runBlocking {
            Zhcs.getInfo()
            Zhcs.sendCode("15960908823")
        }
    }

    @Test
    fun checkActivity() {
        runBlocking {
            Zhcs.getInfo()
            Zhcs.channelActivity()
        }
    }
}