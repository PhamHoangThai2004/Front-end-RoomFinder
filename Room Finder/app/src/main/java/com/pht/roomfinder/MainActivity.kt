package com.pht.roomfinder

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]


        authViewModel.moveFragment.observe(this) {
            authViewModel.errorEmail.value = null
            authViewModel.errorPassword.value = null
            authViewModel.errorMessage.value = null
            authViewModel.errorRepeat.value = null
            authViewModel.showError.value = false

            authViewModel.email.value = null
            authViewModel.password.value = null
            authViewModel.repeatPassword.value = null

            if (it) {
                supportFragmentManager.beginTransaction().replace(R.id.frame_Layout, LoginFragment()).commit()
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.frame_Layout, RegisterFragment()).commit()
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.frame_Layout, LoginFragment()).commit()
    }
}