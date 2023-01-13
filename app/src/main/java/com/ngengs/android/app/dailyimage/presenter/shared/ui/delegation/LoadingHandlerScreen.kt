package com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation

import android.view.View
import com.ngengs.android.app.dailyimage.databinding.LayoutLoadingFullPageBinding
import com.ngengs.android.app.dailyimage.domain.model.Results

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
interface LoadingHandlerScreen {
    fun onHandleLoading(
        mainViewContent: View,
        layoutLoading: LayoutLoadingFullPageBinding,
        data: Results<*>,
        page: Long,
        hasCache: Boolean,
        onDisplayingCache: (() -> Unit)?,
        onLoadingNextPage: () -> Unit,
        onNoLoading: () -> Unit
    )
}