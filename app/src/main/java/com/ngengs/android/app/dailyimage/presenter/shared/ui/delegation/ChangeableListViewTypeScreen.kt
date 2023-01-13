package com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.PhotoListViewType

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
interface ChangeableListViewTypeScreen {
    fun initializeViewTypeScreen(
        context: Context,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        @PhotoListViewType viewType: Int,
        topFullSpanItemCount: () -> Int,
        singleSpanItemCount: () -> Int
    )

    fun currentSpanSize(): Int

    fun onHandleLayoutType(
        @PhotoListViewType viewType: Int,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        onViewTypeChanged: () -> Unit
    )
}