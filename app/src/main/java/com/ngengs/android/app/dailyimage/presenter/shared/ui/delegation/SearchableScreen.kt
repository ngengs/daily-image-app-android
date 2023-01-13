package com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
interface SearchableScreen {
    fun prepareSearch(
        context: Context,
        searchBar: SearchBar?,
        searchView: SearchView,
        searchViewList: RecyclerView,
        updateSearchBarTextOnSearch: Boolean,
        onTypeHandler: (String) -> Unit,
        onCloseHandler: () -> Unit,
        onOpenHandler: () -> Unit,
        onSearchHandler: (String) -> Unit,
    )

    fun updateSearchSuggestion(suggestionData: List<String>)
}