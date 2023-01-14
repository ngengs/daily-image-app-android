package com.ngengs.android.app.dailyimage.helpers.fake.data.source

import com.ngengs.android.app.dailyimage.data.remote.model.PaginationData
import com.ngengs.android.app.dailyimage.data.remote.model.Photos
import com.ngengs.android.app.dailyimage.data.source.PhotoRemoteDataSource

/**
 * Created by rizky.kharisma on 14/01/23.
 * @ngengs
 */
class FakePhotoRemoteDataSource : PhotoRemoteDataSource {
    var photoList: PaginationData<Photos>? = null
    var searchList: PaginationData<Photos>? = null
    var suggestion: List<String> = emptyList()

    override suspend fun getPhotoList(page: Long, orderBy: String): PaginationData<Photos> =
        photoList ?: throw Exception("No Data")

    override suspend fun search(text: String, page: Long): PaginationData<Photos> =
        searchList ?: throw Exception("No Data")

    override suspend fun searchSuggestion(text: String): List<String> =
        suggestion.toList()

    fun reset() {
        photoList = null
        searchList = null
        suggestion = emptyList()
    }
}