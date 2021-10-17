package com.makeevrserg.tradingviewchart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.makeevrserg.tradingview.adapters.WatchListItem
import com.makeevrserg.tradingviewchart.databinding.WatchItemBinding


class WatchListAdapter(private val clickListener: ItemListener) :
    ListAdapter<WatchListItem, WatchListAdapter.ViewHolder>(
        WatchListDiffCallback()
    ) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: WatchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WatchListItem, clickListener: ItemListener) {
            binding.listItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = WatchItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class WatchListDiffCallback : DiffUtil.ItemCallback<WatchListItem>() {
        override fun areItemsTheSame(
            oldItem: WatchListItem,
            newItem: WatchListItem
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: WatchListItem,
            newItem: WatchListItem
        ): Boolean {
            return oldItem.percentChange==newItem.percentChange
        }
    }

}

class ItemListener(val clickListener: (group: WatchListItem, view: View) -> Unit) {
    fun onClick(group: WatchListItem, view: View) = clickListener(group, view)

}