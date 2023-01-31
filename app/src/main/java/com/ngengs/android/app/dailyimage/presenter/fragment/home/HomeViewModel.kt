package com.ngengs.android.app.dailyimage.presenter.fragment.home

import androidx.lifecycle.viewModelScope
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchSuggestion
import com.ngengs.android.app.dailyimage.presenter.BaseViewModel
import com.ngengs.android.app.dailyimage.presenter.fragment.home.HomeViewModel.ViewData
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.SearchableViewModel
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.SearchableViewModelImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSearchSuggestion: GetSearchSuggestion,
    private val dispatcher: DispatcherProvider,
) : BaseViewModel<ViewData>(ViewData()),
    SearchableViewModel by SearchableViewModelImpl(getSearchSuggestion) {
    private var topSpacing: Int = 0

    init {
        Timber.d("init")
    }

    fun getOrUpdateTopSpacing(newValue: Int): Int {
        if (topSpacing == 0) {
            topSpacing = newValue
        }
        return topSpacing
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
        val searchSuggestion: List<String> = emptyList(),
    )
}