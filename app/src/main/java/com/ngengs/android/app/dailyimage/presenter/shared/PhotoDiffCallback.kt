package com.ngengs.android.app.dailyimage.presenter.shared

import androidx.recyclerview.widget.DiffUtil
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
object PhotoDiffCallback : DiffUtil.ItemCallback<PhotosLocal>() {
    override fun areItemsTheSame(oldItem: PhotosLocal, newItem: PhotosLocal): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PhotosLocal, newItem: PhotosLocal): Boolean {
        return oldItem.id == newItem.id
    }
}