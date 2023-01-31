package com.ngengs.android.app.dailyimage.presenter.fragment.detail

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.viewModelScope
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.usecase.ChangeFavoriteStatusUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteStatusUseCase
import com.ngengs.android.app.dailyimage.presenter.BaseViewModel
import com.ngengs.android.app.dailyimage.presenter.fragment.detail.DetailViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getFavoriteStatusUseCase: GetFavoriteStatusUseCase,
    private val changeFavoriteStatusUseCase: ChangeFavoriteStatusUseCase,
    private val dispatcher: DispatcherProvider,
) : BaseViewModel<ViewData>(ViewData()) {
    @VisibleForTesting
    lateinit var photo: PhotosLocal

    fun set(data: PhotosLocal) {
        photo = data
        checkFavorite()
    }

    private fun checkFavorite() {
        viewModelScope.launch(dispatcher.default()) {
            val isFavorite = getFavoriteStatusUseCase(photo)
            _data.update { it.copy(isFavorite = isFavorite) }
        }
    }

    fun changeFavorite() {
        viewModelScope.launch(dispatcher.default()) {
            val currentStatus = data.value.isFavorite ?: false
            changeFavoriteStatusUseCase(photo, currentStatus)
            _data.update { it.copy(isFavorite = !currentStatus) }
        }
    }

    data class ViewData(
        var isFavorite: Boolean? = null
    )
}