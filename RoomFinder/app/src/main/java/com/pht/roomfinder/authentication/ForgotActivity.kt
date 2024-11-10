package com.pht.roomfinder.authentication

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.ActivityForgotBinding
import com.pht.roomfinder.repositories.AuthRepository
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.ForgotViewModel
import com.pht.roomfinder.viewmodel.ForgotViewModelFactory

class ForgotActivity : AppCompatActivity() {
    private lateinit var bin: ActivityForgotBinding
    lateinit var forgotViewModel: ForgotViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bin = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(bin.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ViewCompat.setOnApplyWindowInsetsListener(bin.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val authRepository = AuthRepository(UserService.accountService)
        forgotViewModel = ViewModelProvider(
            this,
            ForgotViewModelFactory(authRepository)
        )[ForgotViewModel::class.java]
        bin.forgotViewModel = forgotViewModel
        bin.lifecycleOwner = this

        supportFragmentManager.beginTransaction().replace(R.id.frame_Layout, ConfirmEmailFragment())
            .commit()

        val dialog = Const.setDialog(R.layout.dialog_loading, this)
        val dialogOTP = openOTPDialog()

        forgotViewModel.dialogStatus.observe(this) {
            if (it) dialog.show()
            else dialog.dismiss()
        }

        forgotViewModel.otpStatus.observe(this) {
            if (it) {
                dialogOTP.show()
            } else {
                dialogOTP.dismiss()
            }
        }

        move()
    }

    private fun move() {
        forgotViewModel.move.observe(this) {
            forgotViewModel.errorEmail.value = null
            forgotViewModel.errorPassword.value = null
            forgotViewModel.errorMessage.value = null

            when (it) {
                0 -> {
                    finish()
                }

                1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_Layout, ConfirmEmailFragment()).commit()
                }

                else -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_Layout, CreatePasswordFragment()).commit()
                }
            }
        }
    }

    private fun openOTPDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_check_otp)
        dialog.setCancelable(false)

        val window: Window? = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute = window?.attributes
        windowAttribute?.gravity = Gravity.CENTER
        window?.attributes = windowAttribute

        val editTextOTP = dialog.findViewById<EditText>(R.id.edit_Text_OTP)
        val buttonConfirm = dialog.findViewById<TextView>(R.id.button_Confirm)
        val buttonCancel = dialog.findViewById<TextView>(R.id.button_Cancel)
        val textViewErrorOTP = dialog.findViewById<TextView>(R.id.text_View_Error_OTP)
        val textViewResend = dialog.findViewById<TextView>(R.id.text_View_Resend)

        textViewResend.setOnClickListener {
            dialog.dismiss()
            forgotViewModel.resendOTP()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonConfirm.setOnClickListener {
            forgotViewModel.checkOTP(editTextOTP.text.toString().trim())
        }

        forgotViewModel.errorOTP.observe(this) {
            if (it != null) {
                textViewErrorOTP.text = it
            }
        }

        return dialog
    }
}