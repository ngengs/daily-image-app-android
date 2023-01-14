package com.ngengs.android.app.dailyimage.helpers.fake.data.repository

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.data.repository.SearchRepository

class FakeSearchRepository : SearchRepository {
    var searchList: CompletableData<PhotosLocal>? = null
    var searchSuggestion: List<String> = emptyList()

    override suspend fun search(text: String, page: Long): CompletableData<PhotosLocal> =
        searchList ?: throw Exception("No Data")

    override suspend fun searchSuggestion(text: String): List<String> = searchSuggestion.toList()

    fun reset() {
        searchList = null
        searchSuggestion = emptyList()
    }
}