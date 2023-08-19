package com.zipper.fetch.core.ext

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

/**
 *
 * @author zhangzhipeng
 * @date 2023/8/18
 */
val globalHttpClient: OkHttpClient = OkHttpClient.Builder().build()
fun Map<String, Any>.toFormBody(): FormBody {
    val formBuilder = FormBody.Builder()
    forEach { (key, value) ->
        formBuilder.addEncoded(key, value.toString())
    }
    return formBuilder.build()
}

val globalGson = Gson()
fun Any.toJsonRequestBody(): RequestBody {
    return globalGson.toJson(this).toRequestBody(MEDIA_JSON)
}

inline fun <reified T> typeToken(): Type {
    return object : TypeToken<T>(){}.type
}

fun String.toJsonRequestBody(): RequestBody {
    return toRequestBody(MEDIA_JSON)
}
