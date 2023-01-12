package com.ngengs.android.app.dailyimage.utils.network

import com.squareup.moshi.Moshi

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
object MoshiConfig {
    val moshi: Moshi by lazy {
        val moshiBuilder = Moshi.Builder()

        // region Add adapter here eg: Date, etc
        // endRegion

        moshiBuilder.build()
    }
}