package com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation

import android.content.Context
import android.view.View
import com.ngengs.android.app.dailyimage.databinding.LayoutErrorFullPageBinding
import com.ngengs.android.app.dailyimage.domain.model.Results

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
interface ErrorHandlerScreen {
    fun onHandleError(
        context: Context,
        mainViewContent: View,
        layoutError: LayoutErrorFullPageBinding,
        data: Results<*>,
        page: Long,
        onRetry: (() -> Unit)?,
        onErrorNextPage: (() -> Unit)?,
    )
}