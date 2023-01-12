package com.ngengs.android.app.dailyimage.presenter.fragment.home

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.model.CompletableCachedData
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.Companion.anyData
import com.ngengs.android.app.dailyimage.domain.usecase.GetPhotoListUseCase
import com.ngengs.android.app.dailyimage.presenter.BaseViewModel
import com.ngengs.android.app.dailyimage.presenter.fragment.home.HomeViewModel.ViewData
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.PhotoListViewType
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.VIEW_TYPE_GRID
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.VIEW_TYPE_LIST
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPhotoListUseCase: GetPhotoListUseCase,
    private val dispatcher: DispatcherProvider,
) : BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
        reload()
    }

    fun reload() {
        Timber.d("reload")
        _data.update { it.copy(page = 1L) }
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
        safeRunJob(dispatcher.default()) {
            val page = data.value.page
            Timber.d("fetch: $page")
            val oldData = data.value.mainData.anyData()
            val orderType = data.value.orderType
            getPhotoListUseCase(page, orderType, oldData)
                .collect { result ->
                    val cache = if (result is Results.Success && result.data.isCache) {
                        result.data.data
                    } else data.value.cache
                    val nextPage = if (result is Results.Success && !result.data.isCache) {
                        page + 1
                    } else page
                    val mainData = if (result is Results.Success && result.data.isCache) {
                        data.value.mainData
                    } else result
                    _data.update { it.copy(mainData = mainData, page = nextPage, cache = cache) }
                }
        }
    }

    fun getViewType() = data.value.viewType
    fun isOrderByLatest() = data.value.orderType == ApiConstant.ORDER_BY_LATEST

    fun changeViewType() {
        val current = data.value.viewType
        val changeTarget = if (current == VIEW_TYPE_GRID) {
            VIEW_TYPE_LIST
        } else VIEW_TYPE_GRID
        _data.update { it.copy(viewType = changeTarget) }
    }

    fun changeOrderBy() {
        val current = data.value.orderType
        val changeTarget = if (current == ApiConstant.ORDER_BY_LATEST) {
            ApiConstant.ORDER_BY_POPULAR
        } else ApiConstant.ORDER_BY_LATEST
        _data.update { it.copy(orderType = changeTarget) }
        reload()
    }

    data class ViewData(
        @PhotoListViewType val viewType: Int = VIEW_TYPE_GRID,
        val orderType: String = ApiConstant.ORDER_BY_LATEST,
        val page: Long = 1L,
        val cache: List<PhotosLocal> = emptyList(),
        val mainData: Results<CompletableCachedData<PhotosLocal>> = Results.Loading()
    )
}