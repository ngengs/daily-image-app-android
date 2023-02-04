package com.ngengs.android.app.dailyimage.presenter.fragment.search

import androidx.lifecycle.viewModelScope
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.Companion.anyData
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchSuggestion
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchedPhotoUseCase
import com.ngengs.android.app.dailyimage.presenter.BaseViewModel
import com.ngengs.android.app.dailyimage.presenter.fragment.search.SearchViewModel.ViewData
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.SearchableViewModel
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.SearchableViewModelImpl
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.PhotoListViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchedPhotoUseCase: GetSearchedPhotoUseCase,
    private val getSearchSuggestion: GetSearchSuggestion,
    private val dispatcher: DispatcherProvider,
) : BaseViewModel<ViewData>(ViewData()),
    SearchableViewModel by SearchableViewModelImpl(getSearchSuggestion) {

    fun setText(text: String) {
        if (data.value.text == text) return
        _data.update { it.copy(text = text) }
        reload()
    }

    fun reload() {
        _data.update { it.copy(page = 1L, mainData = Results.Loading()) }
        fetch()
    }

    fun fetchNextIfNeeded() {
        val isLastPage = data.value.mainData.anyData()?.isComplete == true
        val mainData = data.value.mainData
        Timber.d("fetchNextIfNeeded: $isLastPage")
        if (mainData !is Results.Loading && !isLastPage) {
            fetch()
        }
    }

    private fun fetch() {
        val cleanText = data.value.text?.trim() ?: return
        safeRunJob(dispatcher.default()) {
            val page = data.value.page
            Timber.d("fetch: $page")
            val oldData = data.value.mainData.anyData()
            getSearchedPhotoUseCase(cleanText, page, oldData).collect { result ->
                val nextPage = if (result is Results.Success) {
                    page + 1
                } else {
                    page
                }
                _data.update { it.copy(page = nextPage, mainData = result) }
            }
        }
    }

    fun getViewType() = data.value.viewType

    fun changeViewType() {
        val current = data.value.viewType
        val changeTarget = if (current == ViewConstant.VIEW_TYPE_GRID) {
            ViewConstant.VIEW_TYPE_LIST
        } else {
            ViewConstant.VIEW_TYPE_GRID
        }
        _data.update { it.copy(viewType = changeTarget) }
    }

    fun onTypedSearch(text: String) {
        performSearchSuggestion(text, viewModelScope, dispatcher.default()) { suggestion ->
            _data.update { it.copy(searchSuggestion = suggestion) }
        }
    }

    fun resetSearchSuggestion() {
        _data.update { it.copy(searchSuggestion = emptyList()) }
    }

    data class ViewData(
        var text: String? = null,
        @PhotoListViewType val viewType: Int = ViewConstant.VIEW_TYPE_GRID,
        var page: Long = 1L,
        var mainData: Results<CompletableCachedData<PhotosLocal>> = Results.Loading(),
        val searchSuggestion: List<String> = emptyList(),
    )
}