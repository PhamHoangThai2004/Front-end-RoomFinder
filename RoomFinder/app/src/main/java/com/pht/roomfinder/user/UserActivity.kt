package com.pht.roomfinder.user

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pht.roomfinder.R
import com.pht.roomfinder.model.User
import com.pht.roomfinder.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var userViewModel: UserViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SourceLockedOrientationActivity", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_Container_View) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottom_Navigation_View)

        val intent = intent
        val user: User? = intent.getSerializableExtra("user", User::class.java)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.user.value = user

        setBottomNavigation()

        setFragment()

        userViewModel.isShowBottomNavigation.observe(this) {
            if (it) showBottomNavigation(true)
            else showBottomNavigation(false)
        }

    }

    private fun setBottomNavigation() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> bottomNavigationView.menu.findItem(R.id.menu_home).isChecked =
                    true

                R.id.postFragment -> bottomNavigationView.menu.findItem(R.id.menu_post).isChecked =
                    true

                R.id.favoriteFragment -> bottomNavigationView.menu.findItem(R.id.menu_favorite).isChecked =
                    true

                R.id.settingFragment -> bottomNavigationView.menu.findItem(R.id.menu_setting).isChecked =
                    true
            }
        }
    }

    private fun setFragment() {
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                (R.id.menu_home) -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                (R.id.menu_post) -> {
                    navController.navigate(R.id.postFragment)
                    true
                }

                (R.id.menu_favorite) -> {
                    navController.navigate(R.id.favoriteFragment)
                    true
                }

                else -> {
                    navController.navigate(R.id.settingFragment)
                    true
                }

            }
        }
    }

    private fun showBottomNavigation(isShow: Boolean) {
        lifecycleScope.launch {
            delay(10)
            if (isShow) bottomNavigationView.visibility = BottomNavigationView.VISIBLE
            else bottomNavigationView.visibility = BottomNavigationView.GONE
        }
    }

}