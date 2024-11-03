package com.pht.roomfinder

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.authentication.ForgotActivity
import com.pht.roomfinder.authentication.LoginFragment
import com.pht.roomfinder.authentication.RegisterFragment
import com.pht.roomfinder.repositories.AuthRepository
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.user.UserActivity
import com.pht.roomfinder.viewmodel.AuthViewModel
import com.pht.roomfinder.viewmodel.AuthViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val authRepository = AuthRepository(UserService.accountService)
        authViewModel = ViewModelProvider(this,
            AuthViewModelFactory(application, authRepository))[AuthViewModel::class.java]

        supportFragmentManager.beginTransaction().replace(R.id.frame_Layout, LoginFragment()).commit()

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
                0 -> { supportFragmentManager.beginTransaction().replace(R.id.frame_Layout, LoginFragment()).commit() }
                1 -> { supportFragmentManager.beginTransaction().replace(R.id.frame_Layout, RegisterFragment()).commit() }
                2 -> {
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                }
                else -> { startActivity(Intent(this, ForgotActivity::class.java)) }
            }
        }
    }

}