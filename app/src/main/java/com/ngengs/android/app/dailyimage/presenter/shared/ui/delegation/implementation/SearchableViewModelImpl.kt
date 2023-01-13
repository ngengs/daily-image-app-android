package com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation

import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchSuggestion
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.SearchableViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
class SearchableViewModelImpl(
    private val getSearchSuggestion: GetSearchSuggestion,
) : SearchableViewModel {
    private var suggestionJob: Job? = null

    override fun performSearchSuggestion(
        text: String,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onSuggestionResult: (List<String>) -> Unit
    ) {
        if (suggestionJob?.isActive == true) suggestionJob?.cancel()
        suggestionJob = scope.launch(dispatcher) {
            val searchSuggestion = getSearchSuggestion(text)
            onSuggestionResult(searchSuggestion)
        }
    }
}