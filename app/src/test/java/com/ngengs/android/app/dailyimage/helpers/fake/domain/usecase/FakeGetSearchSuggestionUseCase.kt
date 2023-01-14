package com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase

import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchSuggestion

class FakeGetSearchSuggestionUseCase: GetSearchSuggestion {
    var suggestionList: List<String> = emptyList()

    override suspend fun invoke(text: String): List<String> = suggestionList.toList()

    fun reset() {
        suggestionList = emptyList()
    }
}