package com.ngengs.android.app.dailyimage.data.repository.implementation

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.repository.FavoriteRepository
import com.ngengs.android.app.dailyimage.data.source.PhotoLocalDataSource
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by rizky.kharisma on 20/01/23.
 * @ngengs
 */
class FavoriteRepositoryImpl @Inject constructor(
    private val localDataSource: PhotoLocalDataSource,
    private val dispatcher: DispatcherProvider,
) : FavoriteRepository {
    override fun get(): Flow<List<PhotosLocal>> = localDataSource.getFavorites().onEach {
        Timber.d("get, thread: ${Thread.currentThread().name}")
    }

    override suspend fun isFavorite(data: PhotosLocal): Boolean = withContext(dispatcher.io()) {
        Timber.d("isFavorite, thread: ${Thread.currentThread().name}")
        val favoritePhoto = localDataSource.getFavorite(data.id)
        favoritePhoto != null
    }

    override suspend fun setFavorite(data: PhotosLocal) = withContext(dispatcher.io()) {
        Timber.d("setFavorite, thread: ${Thread.currentThread().name}")
        localDataSource.saveFavorite(data)
    }

    override suspend fun removeFavorite(data: PhotosLocal) = withContext(dispatcher.io()) {
        Timber.d("removeFavorite, thread: ${Thread.currentThread().name}")
        localDataSource.deleteFavorite(data)
    }
}