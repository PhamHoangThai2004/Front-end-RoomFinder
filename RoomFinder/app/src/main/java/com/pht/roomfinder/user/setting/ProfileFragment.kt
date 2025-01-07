package com.pht.roomfinder.user.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.databinding.FragmentProfileBinding
import com.pht.roomfinder.viewmodel.UserViewModel

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

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.isShowBottomNavigation.value = true
    }


}