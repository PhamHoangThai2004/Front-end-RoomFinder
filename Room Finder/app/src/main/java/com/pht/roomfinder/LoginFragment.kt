package com.pht.roomfinder

import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pht.roomfinder.api.AccountService
import com.pht.roomfinder.databinding.FragmentLoginBinding
import com.pht.roomfinder.users.home.HomeActivity
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private lateinit var bin: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        bin = FragmentLoginBinding.inflate(inflater, container, false)
        bin.authViewModel = authViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        sharedPreferences = requireActivity().getSharedPreferences("data", Application.MODE_PRIVATE)
        loadingDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
        }

//        directLogin()

        loginSuccess()

        return bin.root
    }

    private fun directLogin() {
        val token: String? = sharedPreferences.getString("token", null)
        if(token != null) {
            authViewModel.directLogin()
        }
    }

    private fun loginSuccess() {
        authViewModel.intentEvent.observe(viewLifecycleOwner) {
            startActivity(it)
            requireActivity().finish()
        }
    }

}