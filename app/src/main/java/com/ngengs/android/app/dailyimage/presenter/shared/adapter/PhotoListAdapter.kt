package com.ngengs.android.app.dailyimage.presenter.shared.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.ext.imageLarge
import com.ngengs.android.app.dailyimage.data.model.ext.imageLoadingThumb
import com.ngengs.android.app.dailyimage.data.model.ext.imageSmall
import com.ngengs.android.app.dailyimage.databinding.ItemPhotoGridBinding
import com.ngengs.android.app.dailyimage.databinding.ItemPhotoListBinding
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.PhotoListViewType
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.VIEW_TYPE_GRID
import com.ngengs.android.app.dailyimage.utils.image.GlideRequest
import com.ngengs.android.app.dailyimage.utils.image.GlideUtils
import com.ngengs.android.app.dailyimage.utils.ui.TransitionUtils
import com.ngengs.android.app.dailyimage.utils.ui.ext.load
import com.ngengs.android.app.dailyimage.utils.ui.ext.visibleIf

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
class PhotoListAdapter(
    @PhotoListViewType private var viewType: Int,
    private val onClickListener: (PhotosLocal, View) -> Unit,
) : ListAdapter<PhotosLocal, RecyclerView.ViewHolder>(PhotoDiffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = getItem(position).id.hashCode().toLong() + 1000L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (this.viewType == VIEW_TYPE_GRID) {
            val binding = ItemPhotoGridBinding.inflate(layoutInflater, parent, false)
            GridViewHolder(binding)
        } else {
            val binding = ItemPhotoListBinding.inflate(layoutInflater, parent, false)
            ListViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val photo = getItem(position)
        val loadingDrawable = GradientDrawable().apply {
            setColor(Color.parseColor(photo.color))
            alpha = 125
        }
        val thumbnailImage =
            GlideUtils.thumbnailBuilder(holder.itemView.context, photo.imageLoadingThumb)
        if (holder is PhotoListViewHolder) {
            holder.bind(photo, loadingDrawable, thumbnailImage, onClickListener)
        }
        if (holder is GridViewHolder) {
            holder.updateSpacing(position)
        }
    }

    fun updateViewType(@PhotoListViewType viewType: Int) {
        if (this.viewType == viewType) return

        this.viewType = viewType
        notifyItemRangeChanged(0, itemCount)
    }

    class ListViewHolder(private val binding: ItemPhotoListBinding) :
        RecyclerView.ViewHolder(binding.root), PhotoListViewHolder {

        override fun bind(
            data: PhotosLocal,
            loadingDrawable: Drawable,
            thumbnailImage: GlideRequest<Drawable>,
            onCLickListener: (PhotosLocal, View) -> Unit,
        ) {
            binding.fullName.text = data.user?.name
            binding.description.visibleIf(data.description != null)
            binding.description.text = data.description
            val imageUrl = if (binding.root.resources.getBoolean(R.bool.list_use_high_quality)) {
                data.imageLarge
            } else {
                data.imageSmall
            }
            binding.photo.load(imageUrl) {
                thumbnail = thumbnailImage
                imageOnLoadingDrawable = loadingDrawable
            }
            binding.photo.transitionName = TransitionUtils.imageTransitionName(data.id)
            binding.root.setOnClickListener { onCLickListener.invoke(data, binding.photo) }
        }
    }

    class GridViewHolder(private val binding: ItemPhotoGridBinding) :
        RecyclerView.ViewHolder(binding.root), PhotoListViewHolder {

        override fun bind(
            data: PhotosLocal,
            loadingDrawable: Drawable,
            thumbnailImage: GlideRequest<Drawable>,
            onCLickListener: (PhotosLocal, View) -> Unit,
        ) {
            binding.fullName.text = data.user?.name
            binding.description.visibleIf(data.description != null)
            binding.description.text = data.description
            val imageRadius = binding.photo.resources.getDimension(R.dimen.corner_default_size)
            binding.root.background = (loadingDrawable as GradientDrawable).apply {
                cornerRadius = imageRadius
            }
            binding.root.clipToOutline = true
            binding.photo.load(data.imageSmall) {
                thumbnail = thumbnailImage
            }
            binding.photo.transitionName = TransitionUtils.imageTransitionName(data.id)
            binding.root.setOnClickListener { onCLickListener.invoke(data, binding.photo) }
        }

        fun updateSpacing(position: Int) {
            val spacing16 = binding.root.resources.getDimension(R.dimen.spacing_8).toInt()
            val spacing4 = binding.root.resources.getDimension(R.dimen.spacing_4).toInt()
            binding.root.updateLayoutParams<MarginLayoutParams> {
                if (position % 2 != 0) {
                    marginEnd = spacing16
                    marginStart = spacing4
                } else {
                    marginStart = spacing16
                    marginEnd = spacing4
                }
            }
        }
    }

    interface PhotoListViewHolder {
        fun bind(
            data: PhotosLocal,
            loadingDrawable: Drawable,
            thumbnailImage: GlideRequest<Drawable>,
            onCLickListener: (PhotosLocal, View) -> Unit,
        )
    }
}