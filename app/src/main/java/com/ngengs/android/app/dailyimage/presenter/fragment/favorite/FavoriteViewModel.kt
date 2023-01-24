package com.ngengs.android.app.dailyimage.presenter.fragment.favorite

import androidx.lifecycle.viewModelScope
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteListUseCase
import com.ngengs.android.app.dailyimage.presenter.BaseViewModel
import com.ngengs.android.app.dailyimage.presenter.fragment.favorite.FavoriteViewModel.ViewData
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.PhotoListViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteListUseCase: GetFavoriteListUseCase,
    private val dispatcher: DispatcherProvider,
) : BaseViewModel<ViewData>(ViewData()) {

    init {
        viewModelScope.launch(dispatcher.default()) {
            getFavoriteListUseCase.invoke().collect { result ->
                Timber.d("collect, thread: ${Thread.currentThread().name}")
                _data.update { it.copy(mainData = result) }
            }
        }
    }

    fun getViewType() = data.value.viewType

    fun changeViewType() {
        val current = data.value.viewType
        val changeTarget = if (current == ViewConstant.VIEW_TYPE_GRID) {
            ViewConstant.VIEW_TYPE_LIST
        } else ViewConstant.VIEW_TYPE_GRID
        _data.update { it.copy(viewType = changeTarget) }
    }

    data class ViewData(
        @PhotoListViewType val viewType: Int = ViewConstant.VIEW_TYPE_GRID,
        val mainData: Results<CompletableData<PhotosLocal>> = Results.Loading(),
    )
}