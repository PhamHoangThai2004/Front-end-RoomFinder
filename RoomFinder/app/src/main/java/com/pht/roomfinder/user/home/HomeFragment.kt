package com.pht.roomfinder.user.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentHomeBinding
import com.pht.roomfinder.user.UserActivity
import com.pht.roomfinder.viewmodel.UserViewModel

class HomeFragment : Fragment() {
    private lateinit var bin: FragmentHomeBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userViewModel = (activity as UserActivity).userViewModel
        bin = FragmentHomeBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.userViewModel = userViewModel

        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.frame_Layout, ListFragment()).commit()

        return bin.root
    }

}