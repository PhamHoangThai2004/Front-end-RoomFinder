package com.pht.roomfinder.login

import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pht.roomfinder.R
import com.pht.roomfinder.api.LoginService
import com.pht.roomfinder.databinding.FragmentLoginBinding
import com.pht.roomfinder.users.home.HomeActivity
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private lateinit var bin: FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        bin = FragmentLoginBinding.inflate(inflater, container, false)
        bin.loginViewModel = loginViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        sharedPreferences = requireActivity().getSharedPreferences("data", Application.MODE_PRIVATE)
        loadingDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
        }


        directLogin()
        loginSuccess()

        return bin.root
    }

    private fun directLogin() {
        val token: String? = sharedPreferences.getString("token", null)
        if (token != null) {
            lifecycleScope.launch {
                try {
                    loadingDialog.show()
                    val response = LoginService.loginService.checkToken(token)
//                    loadingDialog.dismiss()
                    if (response.status) {
                        val intent = Intent(requireContext(), HomeActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                } catch (e: Exception) {
                    Log.d("BBB", e.message.toString())
                } finally {
                    loginViewModel.loginType.value = false
                }
            }
        }
    }

    private fun loginSuccess() {
        loginViewModel.loginType.observe(viewLifecycleOwner) {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

}