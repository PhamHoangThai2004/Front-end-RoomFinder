package com.pht.roomfinder.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.databinding.FragmentChangePasswordBinding
import com.pht.roomfinder.viewmodel.UserViewModel

class ChangePasswordFragment : Fragment() {
    private lateinit var bin: FragmentChangePasswordBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        bin = FragmentChangePasswordBinding.inflate(inflater, container, false)
        bin.userViewModel = userViewModel
        bin.lifecycleOwner = this

        return bin.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    userViewModel.popBack()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.setShowBottomNavigation(true)
    }

}