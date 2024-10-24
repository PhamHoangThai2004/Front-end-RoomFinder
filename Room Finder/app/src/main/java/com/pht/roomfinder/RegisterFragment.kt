package com.pht.roomfinder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

        authViewModel.otpMessage.observe(viewLifecycleOwner) {
            showDialog(it)
        }

        return bin.root
    }

    private fun registerSuccess() {
        authViewModel.intentEvent.observe(viewLifecycleOwner) {
            startActivity(it)
            requireActivity().finish()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showDialog(message: String) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_check_otp, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)

        val tvOtpMessage = dialogView.findViewById<TextView>(R.id.text_View_Message)
        val etOtp = dialogView.findViewById<EditText>(R.id.edit_Text_OTP)

        tvOtpMessage.text = message

        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}