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
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pht.roomfinder.R
import com.pht.roomfinder.adapters.UserAdapter
import com.pht.roomfinder.model.User
import com.pht.roomfinder.viewmodel.UserViewModel

class UserActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var userViewModel: UserViewModel

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

        viewPager = findViewById(R.id.view_Pager)
        bottomNavigationView = findViewById(R.id.bottom_Navigation_View)

        val intent = intent
        val user: User? = intent.getSerializableExtra("user", User::class.java)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        userViewModel.user.value = user

        viewPager.adapter = UserAdapter(this)
        viewPager.setCurrentItem(0, false)


        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                (R.id.menu_home) -> {
                    viewPager.setCurrentItem(0, true)
                    true
                }

                (R.id.menu_post) -> {
                    viewPager.setCurrentItem(1, true)
                    true
                }

                (R.id.menu_favorite) -> {
                    viewPager.setCurrentItem(2, true)
                    true
                }

                else -> {
                    viewPager.setCurrentItem(3, true)
                    true
                }

            }
        }

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> bottomNavigationView.menu.findItem(R.id.menu_home).setChecked(true)
                    1 -> bottomNavigationView.menu.findItem(R.id.menu_post).setChecked(true)
                    2 -> bottomNavigationView.menu.findItem(R.id.menu_favorite).setChecked(true)
                    else -> bottomNavigationView.menu.findItem(R.id.menu_profile).setChecked(true)
                }
            }
        })

    }

}