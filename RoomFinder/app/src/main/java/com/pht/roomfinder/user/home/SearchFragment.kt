package com.pht.roomfinder.user.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.SearchAdapter
import com.pht.roomfinder.databinding.FragmentSearchBinding
import com.pht.roomfinder.utils.Const
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

        val dialogLoading = Const.setDialog(R.layout.dialog_loading, requireContext())

        setDialogLoading(dialogLoading)

        getListSearch()

        return bin.root
    }

    private fun setDialogLoading(dialogLoading: Dialog) {
        homeViewModel.isLoad.observe(viewLifecycleOwner) {
            if (it) dialogLoading.show()
            else dialogLoading.dismiss()
        }
    }

    private fun getListSearch() {
        if (homeViewModel.listSearch.value != null && homeViewModel.listSearch.value!!.isNotEmpty()) {
            val adapter = SearchAdapter(homeViewModel.listSearch.value!!)
            bin.recyclerViewSearchList.adapter = adapter
            bin.recyclerViewSearchList.layoutManager = LinearLayoutManager(requireContext())
            homeViewModel.isNull.value = false
        } else {
            homeViewModel.isNull.value = true
            homeViewModel.tmpSearch.value = homeViewModel.keySearch.value
        }
    }
}