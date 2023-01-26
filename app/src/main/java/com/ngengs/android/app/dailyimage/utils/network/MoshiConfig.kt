package com.ngengs.android.app.dailyimage.utils.network

import com.ngengs.android.app.dailyimage.utils.network.adapter.NullToEmptyStringAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
object MoshiConfig {
    val moshi: Moshi by lazy {
        val moshiBuilder = Moshi.Builder()

        // region Add adapter here eg: Date, etc
        moshiBuilder.add(NullToEmptyStringAdapter())
        moshiBuilder.add(KotlinJsonAdapterFactory())
        // endRegion

        moshiBuilder.build()
    }
}