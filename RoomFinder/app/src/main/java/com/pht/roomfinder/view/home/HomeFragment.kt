package com.pht.roomfinder.view.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pht.roomfinder.adapters.ListItemAdapter
import com.pht.roomfinder.adapters.NotificationAdapter
import com.pht.roomfinder.databinding.FragmentHomeBinding
import com.pht.roomfinder.helper.DataLocal
import com.pht.roomfinder.view.new_post.NewPostActivity
import com.pht.roomfinder.viewmodel.HomeViewModel
import com.pht.roomfinder.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var bin: FragmentHomeBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateBadge()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

        setItemSelect()

        setListGroupData()

        setListNotification()

        lifecycleScope.launch {
            homeViewModel.uiState.collect {
                if (it.isNotificationVisible) {
                    homeViewModel.getAllNotifications()
                }
            }
        }

        return bin.root
    }

    private fun setListNotification() {
        val adapter = NotificationAdapter(emptyList(), { postId ->
            homeViewModel.setSelectedPost(postId)
        }, { id ->
            homeViewModel.deleteNotification(id)
        })

        bin.recyclerViewListNotification.adapter = adapter
        bin.recyclerViewListNotification.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.listNotification.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    private fun setListGroupData() {
        homeViewModel.listGroupData.observe(viewLifecycleOwner) {
            val adapter = ListItemAdapter(it) { postId ->
                homeViewModel.setSelectedPost(postId)
            }
            bin.recyclerViewParentList.adapter = adapter
            bin.recyclerViewParentList.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setItemSelect() {
        userViewModel.toLayout.observe(viewLifecycleOwner) {
            when (it) {
                UserViewModel.NEW_POST -> {
                    val intent = Intent(requireContext(), NewPostActivity::class.java)
                    intent.putExtra("user", userViewModel.user.value)
                    startActivity(intent)
                }
            }
        }
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

}