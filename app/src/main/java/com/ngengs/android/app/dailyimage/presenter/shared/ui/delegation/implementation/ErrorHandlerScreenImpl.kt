package com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation

import android.content.Context
import android.view.View
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.databinding.LayoutErrorFullPageBinding
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.EMPTY
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.NETWORK
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.SERVER
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.ErrorHandlerScreen
import com.ngengs.android.app.dailyimage.utils.ui.ext.gone
import com.ngengs.android.app.dailyimage.utils.ui.ext.visible
import com.ngengs.android.app.dailyimage.utils.ui.ext.visibleIf

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
class ErrorHandlerScreenImpl : ErrorHandlerScreen {
    override fun onHandleError(
        context: Context,
        mainViewContent: View,
        layoutError: LayoutErrorFullPageBinding,
        data: Results<*>,
        page: Long,
        onRetry: () -> Unit,
        onErrorNextPage: () -> Unit
    ) {
        if (data is Results.Failure) {
            if (page == 1L) {
                mainViewContent.gone()
                layoutError.root.visible()
                val errorType = data.type
                val errorMessage = when (errorType) {
                    EMPTY -> context.getString(R.string.error_message_empty)
                    SERVER -> context.getString(R.string.error_message_server)
                    NETWORK -> context.getString(R.string.error_message_network)
                    else -> context.getString(R.string.error_message_other)
                }
                layoutError.errorMessage.text = errorMessage
                layoutError.retryButton.visibleIf(errorType != EMPTY)
                layoutError.retryButton.setOnClickListener { onRetry() }
            } else {
                onErrorNextPage()
            }
        } else {
            layoutError.root.gone()
        }
    }
}