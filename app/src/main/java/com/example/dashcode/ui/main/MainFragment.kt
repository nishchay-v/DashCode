package com.example.dashcode.ui.main

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dashcode.R
import com.example.dashcode.databinding.AddUserPopupNewBinding
import com.example.dashcode.databinding.MainFragmentBinding
import com.example.dashcode.util.SnapHelperOneByOne
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: MainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        //Recycler Views

        //To have user ratings cards snap in place when scrolling
        val linearSnapHelper = SnapHelperOneByOne()
        linearSnapHelper.attachToRecyclerView(binding.userRecyclerview)

        val userManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val chartAdapter = ChartAdapter()
        binding.userRecyclerview.apply {
            layoutManager = userManager
            adapter = chartAdapter
            isNestedScrollingEnabled = false
        }

        viewModel.userAccounts.observe(viewLifecycleOwner, {
            it?.let {
                chartAdapter.addItemsAndSubmitList(it)
            }
        })

        val cListManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.clistRecyclerview.layoutManager = cListManager
        val cListAdapter = CListAdapter(CListAdapter.LinkClickListener { href ->
            openLink(href)
        })
        binding.clistRecyclerview.adapter = cListAdapter

        viewModel.cList.observe(viewLifecycleOwner, {
            it?.let {
                Log.i("MainFragment", "Contest List Updated with ${it.size} items")
                cListAdapter.addItemsAndSubmitList(it)
            }
        })


        //Add User Popup

        val newPopupBinding: AddUserPopupNewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.add_user_popup_new, null, false)

        val popupDialogNew = Dialog(requireContext())
        popupDialogNew.requestWindowFeature(Window.FEATURE_NO_TITLE)
        popupDialogNew.setContentView(newPopupBinding.root)

        val spinner: Spinner = newPopupBinding.platformsSpinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.platforms_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.onPlatformSelected(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        newPopupBinding.addButton.setOnClickListener {
            val handle = newPopupBinding.userHandle.text.toString()
            viewModel.addUser(handle)
            viewModel.onUserAdded()
        }

        viewModel.foundUser.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it) {
                    Log.i("MainFragment", "User Found")
                    Snackbar.make(requireView(), R.string.account_added, Snackbar.LENGTH_LONG).show()
                }
                else {
                    Log.i("MainFragment", "User NOT Found")
                    Snackbar.make(requireView(), R.string.no_user, Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.showPopup.observe(viewLifecycleOwner, {
            it?.let {
                if (it) popupDialogNew.show()
                else popupDialogNew.dismiss()
            }
        })

        viewModel.navigateToSettings.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it) {
                    findNavController().navigate(R.id.action_mainFragment_to_settingsFragment2)
                    viewModel.onNavigateToSettingsComplete()
                }
            }
        })

        return binding.root
    }

    private fun openLink(href: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(href)
        startActivity(openURL)
    }

    fun getColorRes(colorId: Int) {
        ContextCompat.getColor(requireContext(), colorId)
    }
}