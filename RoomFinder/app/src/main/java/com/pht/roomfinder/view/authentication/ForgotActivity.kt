package com.pht.roomfinder.view.authentication

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.ActivityForgotBinding
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.ForgotViewModel
import kotlinx.coroutines.launch

class ForgotActivity : AppCompatActivity() {
    private lateinit var bin: ActivityForgotBinding
    private lateinit var forgotViewModel: ForgotViewModel

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

        forgotViewModel = ViewModelProvider(this)[ForgotViewModel::class.java]
        bin.forgotViewModel = forgotViewModel
        bin.lifecycleOwner = this

        val dialog = Const.setDialog(R.layout.dialog_loading, this)

        lifecycleScope.launch {
            forgotViewModel.authState.collect {
                if (it.isLoading) dialog.show()
                else dialog.dismiss()
            }
        }

        setLayout()
    }

    private fun setLayout() {
        forgotViewModel.toLayout.observe(this) {
            forgotViewModel.authState.value.password = null
            forgotViewModel.authState.value.errEmail = null
            forgotViewModel.authState.value.errPassword = null
            forgotViewModel.authState.value.message = null

            when (it) {
                ForgotViewModel.LOGIN -> {
                    finish()
                }

                ForgotViewModel.CONFIRM_EMAIL -> {
                    findNavController(R.id.fragment_Container_View).navigate(R.id.confirmEmailFragment)
                }

                ForgotViewModel.CREATE_PASSWORD -> {
                    findNavController(R.id.fragment_Container_View).navigate(R.id.createPasswordFragment)
                }
            }
        }
    }
}