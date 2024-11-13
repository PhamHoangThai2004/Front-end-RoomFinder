package com.pht.roomfinder.user.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
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

        return bin.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment_Container_View) as NavHostFragment
        val controller = navHostFragment.navController
        with(bin) {
            homeViewModel!!.status.observe(viewLifecycleOwner) {
                if (it) controller.navigate(R.id.searchFragment)
            }
            homeViewModel!!.status.observe(viewLifecycleOwner) {
                if (!it) controller.navigate(R.id.listFragment)
            }
        }
    }


}