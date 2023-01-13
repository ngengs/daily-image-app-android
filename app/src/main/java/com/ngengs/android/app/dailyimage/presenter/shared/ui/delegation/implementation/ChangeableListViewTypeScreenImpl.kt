package com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.ChangeableListViewTypeScreen
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.PhotoListViewType

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
class ChangeableListViewTypeScreenImpl : ChangeableListViewTypeScreen {

    private lateinit var gridLayoutManager: GridLayoutManager
    private var spanCount: Int = 2

    @PhotoListViewType
    private var currentViewType: Int = 0

    override fun initializeViewTypeScreen(
        context: Context,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        @PhotoListViewType viewType: Int,
        topFullSpanItemCount: () -> Int,
        singleSpanItemCount: () -> Int
    ) {
        currentViewType = viewType
        spanCount = context.resources.getInteger(R.integer.grid_span)
        gridLayoutManager = GridLayoutManager(context, spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val topItemCount = topFullSpanItemCount()
                    val itemCountWithoutLoadingBottom = topItemCount + singleSpanItemCount()
                    val canTakeAllSpan =
                        position < topItemCount || position >= itemCountWithoutLoadingBottom
                    return if (canTakeAllSpan) {
                        spanCount
                    } else {
                        1
                    }
                }
            }
        }
        updateLayoutManager(viewType, recyclerView, adapter)
    }

    override fun onHandleLayoutType(
        viewType: Int,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        onViewTypeChanged: () -> Unit
    ) {
        val isViewTypeChanged = currentViewType != viewType
        currentViewType = viewType
        if (isViewTypeChanged) {
            onViewTypeChanged()
            updateLayoutManager(viewType, recyclerView, adapter)
        }
    }

    override fun currentSpanSize(): Int = spanCount

    private fun updateLayoutManager(
        viewType: Int,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
    ) {
        recyclerView.layoutManager = gridLayoutManager
        if (viewType == ViewConstant.VIEW_TYPE_GRID) {
            gridLayoutManager.spanCount = spanCount
            recyclerView.adapter = adapter
        } else {
            gridLayoutManager.spanCount = 1
            recyclerView.adapter = adapter
        }
    }
}