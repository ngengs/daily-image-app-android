package com.ngengs.android.app.dailyimage.data.repository

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import kotlinx.coroutines.flow.Flow

/**
 * Created by rizky.kharisma on 20/01/23.
 * @ngengs
 */
interface FavoriteRepository {
    fun get(): Flow<List<PhotosLocal>>
    suspend fun isFavorite(data: PhotosLocal): Boolean
    suspend fun setFavorite(data: PhotosLocal)
    suspend fun removeFavorite(data: PhotosLocal)
}