package com.pht.roomfinder.user.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentHomeBinding
import com.pht.roomfinder.utils.DataLocal
import com.pht.roomfinder.viewmodel.HomeViewModel
import com.pht.roomfinder.viewmodel.UserViewModel

class HomeFragment : Fragment() {
    private lateinit var bin: FragmentHomeBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateBadge()
        }
    }

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

        updateNotification()

        return bin.root
    }

    private fun updateNotification() {
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("UPDATE_BADGE"))
        updateBadge()
    }

    private fun updateBadge() {
        val notificationCount = DataLocal.getInstance().getInt("notification")
        if (notificationCount > 0) {
            bin.homeViewModel!!.notificationCount.value = notificationCount
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
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