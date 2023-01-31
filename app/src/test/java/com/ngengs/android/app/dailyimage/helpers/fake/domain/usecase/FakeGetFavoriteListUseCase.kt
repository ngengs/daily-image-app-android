package com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Created by rizky.kharisma on 31/01/23.
 * @ngengs
 */
class FakeGetFavoriteListUseCase : GetFavoriteListUseCase {
    private val flow: MutableStateFlow<Results<CompletableData<PhotosLocal>>> =
        MutableStateFlow(Results.Loading())

    override fun invoke(): Flow<Results<CompletableData<PhotosLocal>>> = flow.asSharedFlow()
        .distinctUntilChanged()

    suspend fun emit(data: Results<CompletableData<PhotosLocal>>) {
        flow.emit(data)
    }

    suspend fun emit(data: List<PhotosLocal>) {
        flow.emit(Results.Success(CompletableData(true, data)))
    }

    fun reset() {
    }
}