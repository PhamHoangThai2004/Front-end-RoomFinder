package com.pht.roomfinder.authentication

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.MainActivity
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentRegisterBinding
import com.pht.roomfinder.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {
    private lateinit var bin: FragmentRegisterBinding
    private  lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authViewModel = (activity as MainActivity).authViewModel
        bin = FragmentRegisterBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = viewLifecycleOwner
        bin.authViewModel = authViewModel

        authViewModel.otpStatus.observe(viewLifecycleOwner) {
            if (it) openOTPDialog()
        }

        val dialog = Const.setDialog(R.layout.dialog_loading, requireContext())
        authViewModel.dialogStatus.observe(viewLifecycleOwner) {
            if(it) dialog.show()
            else dialog.dismiss()
        }

        return bin.root
    }

    private fun openOTPDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_check_otp)
        dialog.setCancelable(false)

        val window = dialog.window ?: return
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
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
            dialog.dismiss()
            authViewModel.resendOTP()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonConfirm.setOnClickListener {
            authViewModel.checkOTP(editTextOTP.text.toString().trim())
        }

        authViewModel.errorOTP.observe(viewLifecycleOwner) {
            if(it != null) {
                textViewErrorOTP.text = it
            }
        }

        dialog.show()
    }
}