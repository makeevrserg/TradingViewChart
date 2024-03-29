package com.makeevrserg.tradingviewchart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.makeevrserg.tradingview.adapters.WatchListItem
import com.makeevrserg.tradingviewchart.databinding.WatchItemBinding


class WatchListAdapter(private val clickListener: ItemListener) :
    ListAdapter<WatchListItem, WatchListAdapter.ViewHolder>(
        WatchListDiffCallback()
    ), Filterable {

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
            return oldItem.percentChange == newItem.percentChange
        }
    }


    var refList = mutableListOf<WatchListItem>()
    var constList = listOf<WatchListItem>()
    override fun submitList(list: MutableList<WatchListItem>?) {
        super.submitList(list)
        refList = list ?: return
        constList = refList.toList()
    }


    override fun getFilter(): Filter =
        object : Filter() {
            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: Filter.FilterResults
            ) {
                refList.clear()
                refList.addAll(filterResults.values as MutableList<WatchListItem>)
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                return FilterResults().apply {
                    values = constList.filter { it ->
                        it.title.contains(charSequence ?: "")  || it.desc.contains(charSequence ?: "")
                    }
                }
            }
        }


}

class ItemListener(val clickListener: (group: WatchListItem, view: View) -> Unit) {
    fun onClick(group: WatchListItem, view: View) = clickListener(group, view)

}