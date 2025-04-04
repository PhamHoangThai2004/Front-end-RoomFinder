package com.pht.roomfinder.view.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.pht.roomfinder.R
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val dialog = Const.setDialog(R.layout.dialog_loading, this)
        lifecycleScope.launch {
            authViewModel.authState.collect {
                if (it.isLoading) dialog.show()
                else dialog.dismiss()
            }
        }

        toLayout()

        loginSuccess()
    }

    private fun loginSuccess() {
        authViewModel.intentEvent.observe(this) {
            if (it != null) {
                startActivity(it)
                finish()
            }
        }
    }

    private fun toLayout() {
        authViewModel.toLayout.observe(this) {
            authViewModel.authState.value.errEmail = null
            authViewModel.authState.value.errPassword = null
            authViewModel.authState.value.message = null
            authViewModel.authState.value.errName = null
            authViewModel.authState.value.errPhone = null

            authViewModel.authState.value.email = null
            authViewModel.authState.value.password = null
            authViewModel.authState.value.name = null
            authViewModel.authState.value.phone = null

            when (it) {
                AuthViewModel.LOGIN -> {
                    findNavController(R.id.fragment_Container_View).navigate(R.id.loginFragment)
                }

                AuthViewModel.REGISTER -> {
                    findNavController(R.id.fragment_Container_View).navigate(R.id.registerFragment)
                }

                AuthViewModel.FORGOT_PASSWORD -> {
                    startActivity(Intent(this, ForgotActivity::class.java))
                }
            }
        }
    }
}