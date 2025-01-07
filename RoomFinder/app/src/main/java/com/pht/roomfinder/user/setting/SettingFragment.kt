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
        const val OPTION = -1
        const val INFORMATION_ACCOUNT = 0
        const val AVATAR = 1
        const val MY_POST = 2
        const val LOCATION = 3
        const val SAVE_ACCOUNT = 4
        const val CHANGE_PASSWORD = 5
        const val LANGUAGE = 6
        const val THEME = 7
        const val CONTACT_SUPPORT = 8
        const val LOGOUT = 9
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
                OPTION -> {
                    navHostFragment.navController.navigate(R.id.optionFragment)
                }

                INFORMATION_ACCOUNT -> {
                    navHostFragment.navController.navigate(R.id.profileFragment)
                }

                CHANGE_PASSWORD -> {
                    navHostFragment.navController.navigate(R.id.changePasswordFragment)
                }
            }
        }
    }

}