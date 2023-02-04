package com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
interface SearchableViewModel {
    fun performSearchSuggestion(
        text: String,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onSuggestionResult: (List<String>) -> Unit,
    )
}