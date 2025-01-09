package com.pht.roomfinder

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.authentication.AuthActivity
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.utils.DataLocal
import com.pht.roomfinder.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {
    lateinit var authViewModel: AuthViewModel

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

        loginByToken()

        replaceActivity()

    }

    private fun loginByToken() {
        val token = DataLocal.getInstance().getString(Const.TOKEN)
        if (token != null) {
            authViewModel.loginByToken(token)
        }
        else {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun replaceActivity() {
        authViewModel.intentEvent.observe(this) {
            if (it != null) {
                startActivity(authViewModel.intentEvent.value)
                finish()
            } else {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }

        }
    }

}