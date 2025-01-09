package com.pht.roomfinder.user.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        userViewModel.isToast.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    requireContext(),
                    userViewModel.message.value.toString(),
                    Toast.LENGTH_SHORT
                ).show()
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
                    userViewModel.cancelChangePassword()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.isShowBottomNavigation.value = true
    }

}