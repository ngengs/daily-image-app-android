package com.ngengs.android.app.dailyimage.data.repository

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
interface SearchRepository {
    suspend fun search(text: String, page: Long): CompletableData<PhotosLocal>
    suspend fun searchSuggestion(text: String): List<String>
}