package com.pht.roomfinder.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.pht.roomfinder.R
import com.pht.roomfinder.user.UserActivity
import com.pht.roomfinder.viewmodel.AuthViewModel

class AuthActivity : AppCompatActivity() {
    lateinit var authViewModel: AuthViewModel

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

        move()

    }

    private fun move() {
        authViewModel.move.observe(this) {
            authViewModel.errorEmail.value = null
            authViewModel.errorPassword.value = null
            authViewModel.errorMessage.value = null
            authViewModel.errorName.value = null
            authViewModel.errorPhone.value = null

            authViewModel.email.value = null
            authViewModel.password.value = null
            authViewModel.name.value = null
            authViewModel.phoneNumber.value = null

            when (it) {
                0 -> {
                    findNavController(R.id.fragment_Container_View).navigate(R.id.loginFragment)
                }

                1 -> {
                    findNavController(R.id.fragment_Container_View).navigate(R.id.registerFragment)
                }

                2 -> {
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                }

                else -> {
                    startActivity(Intent(this, ForgotActivity::class.java))
                }
            }
        }
    }
}