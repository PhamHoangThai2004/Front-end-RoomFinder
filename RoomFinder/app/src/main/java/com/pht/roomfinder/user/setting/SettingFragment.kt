package com.pht.roomfinder.user.setting

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

    companion object {
        const val OPTION_FRAGMENT = 0
        const val PROFILE_FRAGMENT = 1
        const val CHANGE_PASSWORD_FRAGMENT = 5
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        bin = FragmentSettingBinding.inflate(inflater, container, false)
        bin.userViewModel = userViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        move()

        return bin.root
    }

    fun move() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment_Container_View) as NavHostFragment

        userViewModel.move.observe(viewLifecycleOwner) {
            when (it) {
                OPTION_FRAGMENT -> {
                    navHostFragment.navController.navigate(R.id.optionFragment)
//                    (activity as UserActivity).showBottomNavigation(true)
                    userViewModel.isShowBottomNavigation.value = true
                }

                PROFILE_FRAGMENT -> {
                    navHostFragment.navController.navigate(R.id.profileFragment)
//                    (activity as UserActivity).showBottomNavigation(false)
                    userViewModel.isShowBottomNavigation.value = false
                }

                CHANGE_PASSWORD_FRAGMENT -> {
                    navHostFragment.navController.navigate(R.id.changePasswordFragment)
//                    (activity as UserActivity).showBottomNavigation(false)
                    userViewModel.isShowBottomNavigation.value = false
                }
            }
        }
    }

}