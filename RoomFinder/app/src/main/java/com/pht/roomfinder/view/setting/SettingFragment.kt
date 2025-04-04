package com.pht.roomfinder.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentSettingBinding
import com.pht.roomfinder.viewmodel.UserViewModel

class SettingFragment : Fragment() {
    private lateinit var bin: FragmentSettingBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        bin = FragmentSettingBinding.inflate(inflater, container, false)
        bin.userViewModel = userViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        return bin.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment_Container_View) as NavHostFragment

        userViewModel.toLayout.observe(viewLifecycleOwner) {
            when (it) {
                UserViewModel.OPTION -> {
                    navHostFragment.navController.navigate(R.id.optionFragment)
                }

                UserViewModel.INFORMATION_ACCOUNT -> {
                    navHostFragment.navController.navigate(R.id.profileFragment)
                }

                UserViewModel.AVATAR -> {
                    navHostFragment.navController.navigate((R.id.avatarFragment))
                }

                UserViewModel.CHANGE_PASSWORD -> {
                    navHostFragment.navController.navigate(R.id.changePasswordFragment)
                }
            }
        }
    }

}