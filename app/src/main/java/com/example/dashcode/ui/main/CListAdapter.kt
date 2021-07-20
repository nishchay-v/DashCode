package com.example.dashcode.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dashcode.databinding.ContestItemBinding
import com.example.dashcode.domain.CListContest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_CONTEST = 0

class CListAdapter(val clickListener: LinkClickListener) : ListAdapter<ListItem, RecyclerView.ViewHolder>(ListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_VIEW_TYPE_CONTEST -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolder -> {
                val listItemContests = getItem(position) as ListItem.Contest
                holder.bind(listItemContests.cListContest, clickListener)
            }
        }
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addItemsAndSubmitList(list: List<CListContest>) {
        adapterScope.launch {
            val items = list.map { ListItem.Contest(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class ViewHolder private constructor(private val binding: ContestItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CListContest, clickListener: LinkClickListener) {
            binding.contest = item
            binding.clickListener = clickListener
            //Always a good idea to do this as it can make sizing views a lil quicker
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ContestItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class LinkClickListener(val clickListener: (href: String) -> Unit) {
        fun onClick(contest: CListContest) = clickListener(contest.href)
    }
}


class ListDiffCallback: DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem == newItem
    }

}

sealed class ListItem {
    abstract val id: Int

    data class Contest(val cListContest: CListContest) : ListItem() {
        override val id = cListContest.id
    }
}