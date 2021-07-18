package com.example.dashcode.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dashcode.R
import com.example.dashcode.databinding.AccountListItemBinding
import com.example.dashcode.databinding.ChartItemBinding
import com.example.dashcode.databinding.FragmentAccountListBinding
import com.example.dashcode.databinding.SettingsFragmentBinding
import com.example.dashcode.domain.PlatformUser
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: SettingsFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)

        binding.accountSettings.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_settingsFragment_to_accountListFragment)
        }

        return binding.root
    }

}
