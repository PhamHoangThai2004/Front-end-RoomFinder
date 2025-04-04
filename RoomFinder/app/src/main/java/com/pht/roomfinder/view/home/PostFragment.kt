package com.pht.roomfinder.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.adapters.PostAdapter
import com.pht.roomfinder.databinding.FragmentPostBinding
import com.pht.roomfinder.viewmodel.HomeViewModel

class PostFragment : Fragment() {
    private lateinit var bin: FragmentPostBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        bin = FragmentPostBinding.inflate(inflater, container, false)
        bin.homeViewModel = homeViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        getListCategory()

        openFilter()

        setupRecyclerView()

        homeViewModel.getLists(1)

        return bin.root
    }

    private fun setupRecyclerView() {
        val adapter = PostAdapter(emptyList()) {
            homeViewModel.setSelectedPost(it)
        }
        bin.recyclerViewFilter.adapter = adapter
        bin.recyclerViewFilter.layoutManager = GridLayoutManager(requireContext(), 2)

        homeViewModel.listSearch.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    private fun openFilter() {
        homeViewModel.uiStateLiveData.observe(viewLifecycleOwner) {
            if (it.isOpen) {
                val dialog = FilterFragment()
                dialog.show(parentFragmentManager, "FilterFragment")
            }
        }
    }

    private fun getListCategory() {
        homeViewModel.getListCategory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    homeViewModel.popBack()
                    homeViewModel.openFilter(false)
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.setShowBottomNavigation(true)
    }

}