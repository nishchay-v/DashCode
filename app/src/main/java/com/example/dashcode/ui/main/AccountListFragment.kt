package com.example.dashcode.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dashcode.R
import com.example.dashcode.databinding.AccountListItemBinding
import com.example.dashcode.databinding.FragmentAccountListBinding
import com.example.dashcode.domain.PlatformUser
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountListFragment : Fragment() {

    private val viewModel: AccountListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentAccountListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_list, container, false)
        binding.lifecycleOwner = this

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.accountList.layoutManager = layoutManager
        val adapter = AccountListAdapter(AccountListAdapter.AccountDeleteListener { handle->
            viewModel.deleteUser(handle)
            Snackbar.make(requireView(), R.string.account_removed, Snackbar.LENGTH_LONG).show()
        })
        binding.accountList.adapter = adapter

        viewModel.userAccounts.observe(viewLifecycleOwner, {
            it?.let {
                adapter.addItemsAndSubmitList(it)
            }
        })
        return binding.root
    }
}

class AccountListAdapter(val clickListener: AccountDeleteListener): ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolder -> {
                val account = getItem(position) as DataItem.AccountChartItem
                holder.bind(account.platformUser, clickListener)
            }
        }
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addItemsAndSubmitList(list: List<PlatformUser>) {
        adapterScope.launch {

            val items = list.map { DataItem.AccountChartItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class ViewHolder private constructor(private val binding: AccountListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: PlatformUser, clickListener: AccountDeleteListener) {
            binding.platformUser = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AccountListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class AccountDeleteListener(val clickListener: (userHandle: String) -> Unit) {
        fun onClick(user: PlatformUser) = clickListener(user.handle)
    }
}
