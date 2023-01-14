package com.ngengs.android.app.dailyimage.helpers.fake.data.repository

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.data.repository.PhotoListRepository

class FakePhotoListRepository : PhotoListRepository {
    var photoList: CompletableData<PhotosLocal>? = null
    var cacheList: List<PhotosLocal> = emptyList()

    override suspend fun get(page: Long, orderBy: String): CompletableData<PhotosLocal> =
        photoList ?: throw Exception("No Data")

    override suspend fun cache(orderBy: String): List<PhotosLocal> = cacheList.toList()

    fun reset() {
        photoList = null
        cacheList = emptyList()
    }
}