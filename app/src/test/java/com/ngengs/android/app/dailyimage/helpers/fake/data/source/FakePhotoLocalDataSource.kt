package com.ngengs.android.app.dailyimage.helpers.fake.data.source

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.source.PhotoLocalDataSource

/**
 * Created by rizky.kharisma on 14/01/23.
 * @ngengs
 */
class FakePhotoLocalDataSource : PhotoLocalDataSource {
    var popularPhotos: MutableList<PhotosLocal> = mutableListOf()
    var latestPhotos: MutableList<PhotosLocal> = mutableListOf()

    override suspend fun getPopular(): List<PhotosLocal> =
        popularPhotos.toList()

    override suspend fun savePopular(data: List<PhotosLocal>) {
        popularPhotos.addAll(data)
    }

    override suspend fun clearPopular() {
        popularPhotos.clear()
    }

    override suspend fun getLatest(): List<PhotosLocal> = latestPhotos.toList()

    override suspend fun saveLatest(data: List<PhotosLocal>) {
        latestPhotos.addAll(data)
    }

    override suspend fun clearLatest() {
        latestPhotos.clear()
    }

    fun reset() {
        popularPhotos.clear()
        latestPhotos.clear()
    }
}