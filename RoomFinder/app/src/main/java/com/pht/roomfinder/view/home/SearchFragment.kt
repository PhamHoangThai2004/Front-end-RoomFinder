package com.pht.roomfinder.view.home

//noinspection SuspiciousImport
import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.adapters.PostAdapter
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

        setupRecyclerView()

        homeViewModel.getLists(1)

        homeViewModel.listSearchHistory.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, it)
            bin.listViewSearchHistory.adapter = adapter
        }

        return bin.root
    }

    private fun setupRecyclerView() {
        val adapter = PostAdapter(emptyList()) {
            homeViewModel.setSelectedPost(it)
        }
        bin.recyclerViewSearch.adapter = adapter
        bin.recyclerViewSearch.layoutManager = GridLayoutManager(requireContext(), 2)

        homeViewModel.listSearch.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    homeViewModel.popBack()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.setShowBottomNavigation(true)
    }

}