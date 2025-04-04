package com.pht.roomfinder.view.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.adapters.PostAdapter
import com.pht.roomfinder.databinding.FragmentBonusBinding
import com.pht.roomfinder.viewmodel.HomeViewModel

class BonusFragment : Fragment() {
    private lateinit var bin: FragmentBonusBinding
    private lateinit var homeViewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bin = FragmentBonusBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        bin.homeViewModel = homeViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        setSpinner()

        setupRecyclerView()

        homeViewModel.getLists(2)

        return bin.root
    }

    private fun setupRecyclerView() {
        val adapter = PostAdapter(emptyList()) {
            homeViewModel.setSelectedPost(it)
        }
        bin.recyclerViewBonus.adapter = adapter
        bin.recyclerViewBonus.layoutManager = GridLayoutManager(requireContext(), 2)

        homeViewModel.listSearch.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    private fun setSpinner() {
        bin.spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val area = p0?.getItemAtPosition(p2).toString()
                homeViewModel.getLists(2, area)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

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