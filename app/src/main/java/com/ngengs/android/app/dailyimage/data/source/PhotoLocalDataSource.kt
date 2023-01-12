package com.ngengs.android.app.dailyimage.data.source

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
interface PhotoLocalDataSource {
    suspend fun getPopular(): List<PhotosLocal>
    suspend fun savePopular(data: List<PhotosLocal>)
    suspend fun clearPopular()
    suspend fun getLatest(): List<PhotosLocal>
    suspend fun saveLatest(data: List<PhotosLocal>)
    suspend fun clearLatest()
}