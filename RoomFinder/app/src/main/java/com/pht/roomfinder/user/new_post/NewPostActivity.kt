package com.pht.roomfinder.user.new_post

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.ActivityNewPostBinding
import com.pht.roomfinder.model.User
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.NewPostViewModel

class NewPostActivity : AppCompatActivity() {
    private lateinit var bin: ActivityNewPostBinding
    private lateinit var newPostViewModel: NewPostViewModel

    @SuppressLint("SourceLockedOrientationActivity", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        bin = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(bin.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        newPostViewModel = ViewModelProvider(this)[NewPostViewModel::class.java]
        bin.newPostVM = newPostViewModel

        val intent = intent
        newPostViewModel.user.value = intent.getSerializableExtra("user", User::class.java)

        newPostViewModel.isSuccess.observe(this) {
            finish()
        }

        val dialogLoading = Const.setDialog(R.layout.dialog_loading, this)
        newPostViewModel.isLoading.observe(this) {
            if (it) dialogLoading.show()
            else dialogLoading.dismiss()
        }

        setFragment()

        setTabLayout()

        setNestedScrollView()
    }

    private fun setFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.item_Area, AreaFragment())
            replace(R.id.item_Description, DescriptionFragment())
            replace(R.id.item_Images, ImagesFragment())
        }.commit()

    }

    private fun setNestedScrollView() {
        bin.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            when {
                scrollY >= bin.itemImages.top -> bin.tabLayout.getTabAt(3)?.select()
                scrollY >= bin.itemDescription.top -> bin.tabLayout.getTabAt(2)?.select()
                scrollY >= bin.itemArea.top -> bin.tabLayout.getTabAt(1)?.select()
                scrollY >= bin.itemContact.top -> bin.tabLayout.getTabAt(0)?.select()
            }
        }
    }

    private fun setTabLayout() {
        bin.tabLayout.addTab(bin.tabLayout.newTab().setText("Thông tin liên hệ"))
        bin.tabLayout.addTab(bin.tabLayout.newTab().setText("Khu vực"))
        bin.tabLayout.addTab(bin.tabLayout.newTab().setText("Thông tin mô tả"))
        bin.tabLayout.addTab(bin.tabLayout.newTab().setText("Hình ảnh"))

        bin.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> bin.nestedScrollView.scrollTo(0, bin.itemContact.top)
                    1 -> bin.nestedScrollView.scrollTo(0, bin.itemArea.top)
                    2 -> bin.nestedScrollView.scrollTo(0, bin.itemDescription.top)
                    3 -> bin.nestedScrollView.scrollTo(0, bin.itemImages.top)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

}