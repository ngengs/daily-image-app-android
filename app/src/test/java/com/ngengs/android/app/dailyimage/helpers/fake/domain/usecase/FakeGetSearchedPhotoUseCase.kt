package com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchedPhotoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class FakeGetSearchedPhotoUseCase : GetSearchedPhotoUseCase {
    private val flow: MutableStateFlow<Results<CompletableCachedData<PhotosLocal>>> =
        MutableStateFlow(Results.Loading())

    override suspend fun invoke(
        text: String,
        page: Long,
        oldData: CompletableCachedData<PhotosLocal>?,
    ): Flow<Results<CompletableCachedData<PhotosLocal>>> =
        flow.asSharedFlow().distinctUntilChanged()

    suspend fun emitResult(data: Results<CompletableCachedData<PhotosLocal>>) {
        flow.emit(data)
    }

    fun reset() {
    }
}