package com.ngengs.android.app.dailyimage.helpers.fake.data.source

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.source.PhotoLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Created by rizky.kharisma on 14/01/23.
 * @ngengs
 */
class FakePhotoLocalDataSource : PhotoLocalDataSource {
    var popularPhotos: MutableList<PhotosLocal> = mutableListOf()
    var latestPhotos: MutableList<PhotosLocal> = mutableListOf()
    private val favoritePhotos: MutableList<PhotosLocal> = mutableListOf()
    private val favoriteFlow: MutableStateFlow<List<PhotosLocal>> = MutableStateFlow(emptyList())

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

    override fun getFavorites(): Flow<List<PhotosLocal>> = favoriteFlow.asSharedFlow()
        .distinctUntilChanged()

    override suspend fun getFavorite(id: String): PhotosLocal? {
        return favoritePhotos.find { it.id == id }
    }

    override suspend fun saveFavorite(data: PhotosLocal) {
        favoritePhotos.add(0, data)
        favoriteFlow.emit(favoritePhotos.toList())
    }

    override suspend fun deleteFavorite(data: PhotosLocal) {
        val removed = favoritePhotos.removeIf { it.id == data.id }
        if (removed) favoriteFlow.emit(favoritePhotos.toList())
    }

    fun reset() {
        popularPhotos.clear()
        latestPhotos.clear()
        favoritePhotos.clear()
    }
}