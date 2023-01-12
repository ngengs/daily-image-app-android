package com.ngengs.android.app.dailyimage.presenter.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ngengs.android.app.dailyimage.databinding.ItemLoadingAdapterBinding
import com.ngengs.android.app.dailyimage.presenter.common.LoadingItemAdapter.ViewHolder

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
class LoadingItemAdapter(private val loadingMessage: String) : RecyclerView.Adapter<ViewHolder>() {
    private var isLoading = false

    init {
        setHasStableIds(true)
    }

    override fun getItemCount() = if (isLoading) 1 else 0
    override fun getItemId(position: Int) = loadingMessage.hashCode().toLong() + 10L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadingAdapterBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(loadingMessage)
    }

    fun startLoading() {
        if (isLoading) return
        isLoading = true
        notifyItemInserted(0)
    }

    fun stopLoading() {
        if (!isLoading) return
        isLoading = false
        notifyItemRemoved(0)
    }

    data class ViewHolder(private val binding: ItemLoadingAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(loadingMessage: String) {
            binding.loadingText.text = loadingMessage
        }
    }
}