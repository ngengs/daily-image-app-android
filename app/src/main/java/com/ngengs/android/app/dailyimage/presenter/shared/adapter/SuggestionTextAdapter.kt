package com.ngengs.android.app.dailyimage.presenter.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ngengs.android.app.dailyimage.databinding.ItemSuggestionTextBinding
import com.ngengs.android.app.dailyimage.presenter.shared.adapter.SuggestionTextAdapter.ViewHolder

/**
 * Created by rizky.kharisma on 13/01/23.
 * @ngengs
 */
class SuggestionTextAdapter(private val onClickListener: (String) -> Unit) :
    RecyclerView.Adapter<ViewHolder>() {

    private val data: MutableList<String> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int = data.size
    override fun getItemId(position: Int): Long = data[position].hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSuggestionTextBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], onClickListener)
    }

    fun update(data: List<String>) {
        val currentSize = itemCount
        val newSize = data.size
        this.data.clear()
        this.data.addAll(data)
        if (currentSize == newSize) {
            notifyItemRangeChanged(0, newSize)
        } else if (currentSize < newSize) {
            notifyItemRangeChanged(0, currentSize)
            notifyItemRangeInserted(currentSize, newSize - currentSize)
        } else {
            notifyItemRangeChanged(0, newSize)
            notifyItemRangeRemoved(newSize, currentSize - newSize)
        }
    }

    data class ViewHolder(private val binding: ItemSuggestionTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String, clickListener: (String) -> Unit) {
            binding.itemText.text = text
            binding.root.setOnClickListener { clickListener(text) }
        }
    }
}