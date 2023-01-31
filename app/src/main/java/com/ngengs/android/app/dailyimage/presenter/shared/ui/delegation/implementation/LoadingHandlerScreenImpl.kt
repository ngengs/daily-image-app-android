package com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation

import android.view.View
import com.ngengs.android.app.dailyimage.databinding.LayoutLoadingFullPageBinding
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.LoadingHandlerScreen
import com.ngengs.android.app.dailyimage.utils.ui.ext.gone
import com.ngengs.android.app.dailyimage.utils.ui.ext.visible

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
class LoadingHandlerScreenImpl : LoadingHandlerScreen {
    override fun onHandleLoading(
        mainViewContent: View,
        layoutLoading: LayoutLoadingFullPageBinding,
        data: Results<*>,
        page: Long,
        hasCache: Boolean,
        onDisplayingCache: (() -> Unit)?,
        onLoadingNextPage: (() -> Unit)?,
        onNoLoading: (() -> Unit)?
    ) {
        if (data is Results.Loading) {
            if (page == 1L && !hasCache) {
                mainViewContent.gone()
                layoutLoading.root.visible()
            } else if (page == 1L) {
                layoutLoading.root.gone()
                onDisplayingCache?.invoke()
            } else {
                layoutLoading.root.gone()
                onLoadingNextPage?.invoke()
            }
        } else {
            layoutLoading.root.gone()
            onNoLoading?.invoke()
        }
    }
}