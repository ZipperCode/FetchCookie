package com.zipper.fetch.core.ext

import okhttp3.FormBody

/**
 *
 * @author zhangzhipeng
 * @date 2023/8/18
 */

fun Map<String, Any>.toFormBody(): FormBody {
    val formBuilder = FormBody.Builder()
    forEach { (key, value) ->
        formBuilder.addEncoded(key, value.toString())
    }
    return formBuilder.build()
}
