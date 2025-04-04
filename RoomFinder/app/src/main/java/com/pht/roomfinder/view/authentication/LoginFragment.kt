package com.pht.roomfinder.view.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.databinding.FragmentLoginBinding
import com.pht.roomfinder.helper.DataLocal
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var bin: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        bin = FragmentLoginBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.authViewModel = authViewModel

        return bin.root
    }

    // Hàm này dùng để test khi MainActivity bị lỗi
    private fun directLogin() {
        val token = DataLocal.getInstance().getString(Const.TOKEN)
        if (token != null) {
            authViewModel.loginByToken(token)
        }
    }

}