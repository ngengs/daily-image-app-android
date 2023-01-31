package com.ngengs.android.app.dailyimage.helpers.fake.data.repository

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Created by rizky.kharisma on 26/01/23.
 * @ngengs
 */
class FakeFavoriteRepository : FavoriteRepository {
    private val favoritePhotos: MutableList<PhotosLocal> = mutableListOf()
    private val favoriteFlow: MutableStateFlow<List<PhotosLocal>> = MutableStateFlow(emptyList())

    override fun get(): Flow<List<PhotosLocal>> = favoriteFlow.asSharedFlow().distinctUntilChanged()

    override suspend fun isFavorite(data: PhotosLocal): Boolean {
        return favoritePhotos.find { it.id == data.id } != null
    }

    override suspend fun setFavorite(data: PhotosLocal) {
        favoritePhotos.add(0, data)
        favoriteFlow.emit(favoritePhotos.toList())
    }

    override suspend fun removeFavorite(data: PhotosLocal) {
        val removed = favoritePhotos.removeIf { it.id == data.id }
        if (removed) favoriteFlow.emit(favoritePhotos.toList())
    }

    suspend fun addFavorite(data: List<PhotosLocal>) {
        favoritePhotos.addAll(0, data)
        favoriteFlow.emit(data)
    }

    fun reset() {
        favoritePhotos.clear()

    }
}