package com.zipper.fetch.cookie.dao

import androidx.room.TypeConverter
import com.zipper.fetch.cookie.ui.minimt.model.MiniTokenData
import com.zipper.fetch.core.ext.globalGson

/**
 *
 * @author zhangzhipeng
 * @date 2023/8/22
 */
class MiniTokenDataConverters {

    @TypeConverter
    fun string2Object(value: String?): MiniTokenData? {
        return globalGson.fromJson(value, MiniTokenData::class.java)
    }

    @TypeConverter
    fun object2String(miniTokenData: MiniTokenData?): String? {
        return globalGson.toJson(miniTokenData)
    }
}
