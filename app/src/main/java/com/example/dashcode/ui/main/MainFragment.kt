package com.example.dashcode.ui.main

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dashcode.R
import com.example.dashcode.databinding.AddUserPopupNewBinding
import com.example.dashcode.databinding.MainFragmentBinding
import com.example.dashcode.util.SnapHelperOneByOne

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

        viewModel.codeForcesUsers.observe(viewLifecycleOwner, {
            it?.let {
                chartAdapter.addItemsAndSubmitList(it)
            }
        })

        val cListManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.clistRecyclerview.layoutManager = cListManager
        val cListAdapter = CListAdapter()
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

        viewModel.showPopup.observe(viewLifecycleOwner, {
            it?.let {
                if (it) popupDialogNew.show()
                else popupDialogNew.dismiss()
            }
        })

        return binding.root
    }

}