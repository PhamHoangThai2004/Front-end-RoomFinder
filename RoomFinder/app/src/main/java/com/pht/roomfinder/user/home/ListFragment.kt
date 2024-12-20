package com.pht.roomfinder.user.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pht.roomfinder.adapters.ListItemAdapter
import com.pht.roomfinder.databinding.FragmentListBinding
import com.pht.roomfinder.viewmodel.HomeViewModel

class ListFragment : Fragment() {
    private lateinit var bin: FragmentListBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        bin = FragmentListBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.homeViewModel = homeViewModel

        homeViewModel.getListGroupData()
        setListGroupData()

        return bin.root
    }

    private fun setListGroupData() {
        homeViewModel.listGroupData.observe(viewLifecycleOwner) {
            val adapter = ListItemAdapter(it)
            bin.recyclerViewParentList.adapter = adapter
            bin.recyclerViewParentList.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}