package com.pht.roomfinder.user.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pht.roomfinder.adapters.SearchAdapter
import com.pht.roomfinder.databinding.FragmentSearchBinding
import com.pht.roomfinder.viewmodel.HomeViewModel

class SearchFragment : Fragment() {
    private lateinit var bin: FragmentSearchBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        bin = FragmentSearchBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.homeViewModel = homeViewModel

        getListSearch()

        return bin.root
    }

    private fun getListSearch() {
        if (homeViewModel.listSearch.value != null && homeViewModel.listSearch.value!!.isNotEmpty()) {
            val adapter = SearchAdapter(homeViewModel.listSearch.value!!)
            bin.recyclerViewSearchList.adapter = adapter
            bin.recyclerViewSearchList.layoutManager = LinearLayoutManager(requireContext())
            Log.d("BBB", "Không bị null")
            homeViewModel.isNull.value = false
        } else {
            Log.d("BBB", "Bị null")
            homeViewModel.isNull.value = true
            homeViewModel.tmpSearch.value = homeViewModel.keySearch.value
        }
    }
}