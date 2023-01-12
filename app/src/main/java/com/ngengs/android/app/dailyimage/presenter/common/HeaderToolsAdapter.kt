package com.ngengs.android.app.dailyimage.presenter.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.databinding.ItemPhotoHeaderToolsBinding
import com.ngengs.android.app.dailyimage.presenter.common.HeaderToolsAdapter.ViewHolder
import com.ngengs.android.app.dailyimage.utils.ui.ext.visibleIf

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
class HeaderToolsAdapter(
    private var headerTitle: String,
    private var onClickOrderBy: () -> Unit,
    private var onClickViewType: () -> Unit,
) : RecyclerView.Adapter<ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    @DrawableRes
    private var iconOrderBy: Int? = R.drawable.ic_baseline_sort_calendar_desc_24

    @DrawableRes
    private var iconViewType: Int = R.drawable.ic_baseline_grid_view_24

    override fun getItemCount() = 1

    override fun getItemId(position: Int) = 1L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPhotoHeaderToolsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder((binding))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(headerTitle, iconViewType, iconOrderBy, onClickViewType, onClickOrderBy)
    }

    fun changeTitle(text: String) {
        if (headerTitle == text) return
        headerTitle = text
        notifyItemChanged(0)
    }

    fun changeOrderIcon(@DrawableRes icon: Int) {
        if (iconOrderBy == icon) return
        iconOrderBy = icon
        notifyItemChanged(0)
    }

    fun changeViewTypeIcon(@DrawableRes icon: Int) {
        if (iconViewType == icon) return
        iconViewType = icon
        notifyItemChanged(0)
    }

    data class ViewHolder(
        private val binding: ItemPhotoHeaderToolsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            headerTitle: String,
            iconViewType: Int,
            iconOrderBy: Int?,
            onClickViewType: () -> Unit,
            onClickOrderBy: () -> Unit
        ) {
            binding.toolsText.text = headerTitle
            binding.toolsText.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.orderTypeButton.visibleIf(iconOrderBy != null)
            binding.viewTypeButton.icon =
                ContextCompat.getDrawable(binding.root.context, iconViewType)
            iconOrderBy?.let {
                binding.orderTypeButton.icon = ContextCompat.getDrawable(binding.root.context, it)
            }
            binding.viewTypeButton.setOnClickListener { onClickViewType() }
            binding.orderTypeButton.setOnClickListener { onClickOrderBy() }
        }
    }
}