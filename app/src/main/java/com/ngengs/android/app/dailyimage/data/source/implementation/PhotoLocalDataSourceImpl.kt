package com.ngengs.android.app.dailyimage.data.source.implementation

import com.ngengs.android.app.dailyimage.data.local.DailyImageDatabase
import com.ngengs.android.app.dailyimage.data.local.model.FavoritePhotos
import com.ngengs.android.app.dailyimage.data.local.model.LatestPhotos
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.local.model.PopularPhotos
import com.ngengs.android.app.dailyimage.data.source.PhotoLocalDataSource
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
class PhotoLocalDataSourceImpl @Inject constructor(
    private val database: DailyImageDatabase,
    private val dispatcher: DispatcherProvider,
) : PhotoLocalDataSource {
    private val photosDao by lazy { database.photosDao() }
    private val popularDao by lazy { database.popularDao() }
    private val latestDao by lazy { database.latestDao() }
    private val favoriteDao by lazy { database.favoriteDao() }

    override suspend fun getPopular(): List<PhotosLocal> = withContext(dispatcher.io()) {
        Timber.d("getPopular, thread: ${Thread.currentThread().name}")
        popularDao.get().mapNotNull { it.photos }
    }

    override suspend fun savePopular(data: List<PhotosLocal>) = withContext(dispatcher.io()) {
        Timber.d("savePopular, thread: ${Thread.currentThread().name}")
        savePhotos(data)
        popularDao.save(data.map { PopularPhotos(photosId = it.id) })
    }

    override suspend fun clearPopular() = withContext(dispatcher.io()) {
        Timber.d("clearPopular, thread: ${Thread.currentThread().name}")
        safeClear(CLEAR_TYPE_POPULAR)
    }

    override suspend fun getLatest(): List<PhotosLocal> = withContext(dispatcher.io()) {
        Timber.d("getLatest, thread: ${Thread.currentThread().name}")
        latestDao.get().mapNotNull { it.photos }
    }

    override suspend fun saveLatest(data: List<PhotosLocal>) = withContext(dispatcher.io()) {
        Timber.d("saveLatest, thread: ${Thread.currentThread().name}")
        savePhotos(data)
        latestDao.save(data.map { LatestPhotos(photosId = it.id) })
    }

    override suspend fun clearLatest() = withContext(dispatcher.io()) {
        Timber.d("clearLatest, thread: ${Thread.currentThread().name}")
        safeClear(CLEAR_TYPE_LATEST)
    }

    override fun getFavorites(): Flow<List<PhotosLocal>> {
        Timber.d("getFavorite, thread: ${Thread.currentThread().name}")
        return favoriteDao.get()
            .map { data ->
                Timber.d("getFavorite-map, thread: ${Thread.currentThread().name}")
                data.mapNotNull { it.photos }
            }
            .flowOn(dispatcher.io())
    }

    override suspend fun getFavorite(id: String): PhotosLocal? = withContext(dispatcher.io()){
        Timber.d("getFavorite, thread: ${Thread.currentThread().name}")
        favoriteDao.get(id)?.photos
    }

    override suspend fun saveFavorite(data: PhotosLocal) = withContext(dispatcher.io()){
        Timber.d("saveFavorite, thread: ${Thread.currentThread().name}")
        savePhotos(listOf(data))
        favoriteDao.save(FavoritePhotos(photosId = data.id))
    }

    override suspend fun deleteFavorite(data: PhotosLocal) = withContext(dispatcher.io()){
        Timber.d("getFavorite, thread: ${Thread.currentThread().name}")
        safeClear(CLEAR_TYPE_FAVORITE, listOf(data.id))
    }

    private suspend fun savePhotos(data: List<PhotosLocal>) = withContext(dispatcher.io()) {
        Timber.d("savePhotos, thread: ${Thread.currentThread().name}")
        photosDao.save(data)
    }

    private suspend fun safeClear(
        clearType: Int,
        favoriteIdToRemove: List<String> = emptyList()
    ) = withContext(dispatcher.io()) {
        Timber.d("safeClear, thread: ${Thread.currentThread().name}")
        val popularId = getPopular().map { it.id }
        val latestId = getLatest().map { it.id }
        val favoriteData = favoriteDao.getAll()
        val favoriteId = favoriteData.mapNotNull { it.photos?.id }
        val idToRemove = when (clearType) {
            CLEAR_TYPE_POPULAR -> popularId
            CLEAR_TYPE_FAVORITE -> favoriteIdToRemove
            else -> latestId
        }.toMutableList()

        // Clear the table
        when (clearType) {
            CLEAR_TYPE_POPULAR -> {
                popularDao.clear()
                idToRemove.removeAll { latestId.contains(it) }
                idToRemove.removeAll { favoriteId.contains(it) }
            }
            CLEAR_TYPE_FAVORITE -> {
                val favorite =
                    favoriteData.find { it.favorite.photosId == favoriteIdToRemove.first() }
                favorite?.let { favoriteDao.delete(it.favorite) }
                idToRemove.removeAll { latestId.contains(it) }
                idToRemove.removeAll { popularId.contains(it) }
            }
            else -> {
                latestDao.clear()
                idToRemove.removeAll { popularId.contains(it) }
                idToRemove.removeAll { favoriteId.contains(it) }
            }
        }

        // Remove photos data that only used by the cleared table
        photosDao.delete(idToRemove)
    }

    companion object {
        private const val CLEAR_TYPE_POPULAR = 1000
        private const val CLEAR_TYPE_LATEST = 1001
        private const val CLEAR_TYPE_FAVORITE = 1002
    }
}