package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import androidx.annotation.VisibleForTesting
import com.ngengs.android.app.dailyimage.data.repository.SearchRepository
import com.ngengs.android.app.dailyimage.di.DispatcherProvider
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchSuggestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
class GetSearchSuggestionImpl @Inject constructor(
    private val repository: SearchRepository,
    private val dispatcher: DispatcherProvider,
) : GetSearchSuggestion {
    override suspend fun invoke(text: String) = withContext(dispatcher.io()) {
        delay(DELAY_TYPING)
        val cleanText = text.trim()
        if (cleanText.length < LENGTH_THRESHOLD) {
            emptyList()
        } else {
            repository.searchSuggestion(text)
        }
    }

    companion object {
        private const val DELAY_TYPING = 300L

        @VisibleForTesting
        const val LENGTH_THRESHOLD = 3
    }
}