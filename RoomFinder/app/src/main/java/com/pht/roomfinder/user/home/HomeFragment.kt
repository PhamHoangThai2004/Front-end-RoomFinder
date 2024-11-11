package com.pht.roomfinder.user.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentHomeBinding
import com.pht.roomfinder.viewmodel.HomeViewModel
import com.pht.roomfinder.viewmodel.UserViewModel

class HomeFragment : Fragment() {
    private lateinit var bin: FragmentHomeBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        bin = FragmentHomeBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.userViewModel = userViewModel
        bin.homeViewModel = homeViewModel

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_Layout, ListFragment()).commit()

        homeViewModel.listSearch.observe(viewLifecycleOwner) {
            val fragment = SearchFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_Layout, fragment).addToBackStack(null).commit()
        }

        homeViewModel.status.observe(viewLifecycleOwner) {
            if (!it) requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return bin.root
    }


}