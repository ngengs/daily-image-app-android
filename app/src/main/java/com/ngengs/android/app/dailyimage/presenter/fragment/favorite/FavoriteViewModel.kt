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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteListUseCase: GetFavoriteListUseCase,
    private val dispatcher: DispatcherProvider,
) : BaseViewModel<ViewData>(ViewData()) {

    private val _viewType = MutableStateFlow(ViewConstant.VIEW_TYPE_GRID)

    override val data: StateFlow<ViewData>
        get() = getFavoriteListUseCase.invoke()
            .combine(_viewType) { favorites, viewType ->
                Timber.d("combine, thread: ${Thread.currentThread().name}")
                ViewData(viewType = viewType, mainData = favorites)
            }
            .flowOn(dispatcher.default())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = ViewData()
            )

    fun getViewType() = _viewType.value

    fun changeViewType() {
        val current = _viewType.value
        val changeTarget = if (current == ViewConstant.VIEW_TYPE_GRID) {
            ViewConstant.VIEW_TYPE_LIST
        } else ViewConstant.VIEW_TYPE_GRID
        _viewType.update { changeTarget }
    }

    data class ViewData(
        @PhotoListViewType val viewType: Int = ViewConstant.VIEW_TYPE_GRID,
        val mainData: Results<CompletableData<PhotosLocal>> = Results.Loading(),
    )
}