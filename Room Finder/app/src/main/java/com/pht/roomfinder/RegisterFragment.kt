package com.pht.roomfinder

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.databinding.FragmentRegisterBinding
import com.pht.roomfinder.users.home.HomeActivity

class RegisterFragment : Fragment() {
    private lateinit var bin: FragmentRegisterBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        bin = FragmentRegisterBinding.inflate(inflater, container, false)
        bin.authViewModel = authViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        registerSuccess()

        return bin.root
    }

    private fun registerSuccess() {
        authViewModel.intentEvent.observe(viewLifecycleOwner) {
            startActivity(it)
            requireActivity().finish()
        }
    }
}