package com.ngengs.android.app.dailyimage.data.local

import androidx.room.TypeConverter
import com.ngengs.android.app.dailyimage.data.model.UserSimple
import com.ngengs.android.app.dailyimage.utils.network.MoshiConfig

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
class DailyImageDatabaseTypeConverter {
    private val userSimpleAdapter = MoshiConfig.moshi.adapter(UserSimple::class.java)

    @TypeConverter
    fun fromUserSimple(data: UserSimple?): String {
        return if (data == null) ""
        else userSimpleAdapter.toJson(data)
    }

    @TypeConverter
    fun toNutrition(dataString: String): UserSimple? {
        return if (dataString.isEmpty()) null
        else userSimpleAdapter.fromJson(dataString)
    }
}