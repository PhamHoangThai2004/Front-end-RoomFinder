package com.pht.roomfinder.view.authentication

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentConfirmEmailBinding
import com.pht.roomfinder.viewmodel.ForgotViewModel
import kotlinx.coroutines.launch

class ConfirmEmailFragment : Fragment() {
    private lateinit var bin: FragmentConfirmEmailBinding
    private lateinit var forgotViewModel: ForgotViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        forgotViewModel = ViewModelProvider(requireActivity())[ForgotViewModel::class.java]
        bin = FragmentConfirmEmailBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.forgotViewModel = forgotViewModel

        lifecycleScope.launch {
            forgotViewModel.authState.collect {
                if (it.isOpenOTP) otpDialog()
            }
        }

        return bin.root
    }

    private fun otpDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_check_otp)
        dialog.setCancelable(false)


        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        val editTextOTP = dialog.findViewById<EditText>(R.id.edit_Text_OTP)
        val buttonConfirm = dialog.findViewById<TextView>(R.id.button_Confirm)
        val buttonCancel = dialog.findViewById<TextView>(R.id.button_Cancel)
        val textViewErrorOTP = dialog.findViewById<TextView>(R.id.text_View_Error_OTP)
        val textViewResend = dialog.findViewById<TextView>(R.id.text_View_Resend)

        textViewResend.setOnClickListener {
            forgotViewModel.setOTPStatus(false)
            forgotViewModel.resendOTP()
        }

        buttonCancel.setOnClickListener {
            forgotViewModel.setOTPStatus(false)
        }

        buttonConfirm.setOnClickListener {
            forgotViewModel.checkOTP(editTextOTP.text.toString().trim())
        }

        lifecycleScope.launch {
            forgotViewModel.authState.collect {
                if (it.errOTP != null) textViewErrorOTP.text = it.errOTP
            }
        }

        lifecycleScope.launch {
            forgotViewModel.authState.collect {
                if (!it.isOpenOTP) dialog.dismiss()
            }
        }

        dialog.show()
    }

}