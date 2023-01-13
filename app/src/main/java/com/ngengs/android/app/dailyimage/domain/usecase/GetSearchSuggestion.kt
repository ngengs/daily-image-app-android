package com.ngengs.android.app.dailyimage.domain.usecase

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
interface GetSearchSuggestion {
    suspend operator fun invoke(text: String): List<String>
}