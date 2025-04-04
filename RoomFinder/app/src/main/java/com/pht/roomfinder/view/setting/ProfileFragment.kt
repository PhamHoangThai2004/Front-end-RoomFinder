package com.pht.roomfinder.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.databinding.FragmentProfileBinding
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var bin: FragmentProfileBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        bin = FragmentProfileBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.userViewModel = userViewModel

        if (userViewModel.avatar.value.isNullOrEmpty().not()) {
            CoroutineScope(Dispatchers.Main).launch {
                Const.loadImage(
                    requireContext(),
                    userViewModel.user.value?.avatar!!,
                    bin.imageViewAvatar, 1
                )
            }
        }

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
        if (userViewModel.toLayout.value != UserViewModel.AVATAR)
            userViewModel.setShowBottomNavigation(true)
    }


}