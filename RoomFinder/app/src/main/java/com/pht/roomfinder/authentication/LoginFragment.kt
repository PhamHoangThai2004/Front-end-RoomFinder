package com.pht.roomfinder.authentication

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pht.roomfinder.MainActivity
import com.pht.roomfinder.databinding.FragmentLoginBinding
import com.pht.roomfinder.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var bin: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authViewModel = (activity as MainActivity).authViewModel
        bin = FragmentLoginBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.authViewModel = authViewModel
        sharedPreferences = requireActivity().getSharedPreferences("data", Application.MODE_PRIVATE)

        directLogin()

        loginSuccess()

        return bin.root
    }

    private fun directLogin() {
        val token: String? = sharedPreferences.getString("token", null)
        if(token != null) { authViewModel.directLogin(token) }
    }

    private fun loginSuccess() {
        authViewModel.intentEvent.observe(viewLifecycleOwner) {
            startActivity(it)
            requireActivity().finish()
        }
    }

}