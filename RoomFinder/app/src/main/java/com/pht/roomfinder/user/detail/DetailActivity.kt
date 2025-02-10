package com.pht.roomfinder.user.detail

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.pht.roomfinder.R
import com.pht.roomfinder.user.update_post.UpdatePostActivity
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.DetailViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var launcher: ActivityResultLauncher<Intent>

    @SuppressLint("SourceLockedOrientationActivity", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        val intent = intent
        val postId = intent.getIntExtra("postId", -1)
        val userId = intent.getIntExtra("userId", -1)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val id = it.data?.extras?.getInt("post_id", -1)
                if ((id ?: -1) > 0) {
                    detailViewModel.getPostDetail(id!!)
                }
            }
        }

        if (postId > 0) {
            detailViewModel.getPostDetail(postId)
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            finish()
        }

        detailViewModel.postDetail.observe(this) {
            if (it.user?.userId == userId) detailViewModel.isUserPosting.value = true
        }

        detailViewModel.selectedPost.observe(this) {
            findNavController(R.id.fragment_Container_View).popBackStack()
            detailViewModel.getPostDetail(it)
        }

        val dialogLoading = Const.setDialog(R.layout.dialog_loading, this)
        detailViewModel.isLoading.observe(this) {
            if (it) dialogLoading.show()
            else dialogLoading.dismiss()
        }

        changeUI()

    }

    private fun changeUI() {
        detailViewModel.move.observe(this) {
            when (it) {
                DetailViewModel.CONTACT -> {
                    findNavController(R.id.fragment_Container_View).navigate(R.id.contactFragment)
                }

                DetailViewModel.DETAIL -> {
                    findNavController(R.id.fragment_Container_View).popBackStack()
                }

                DetailViewModel.CALL -> {
                    toPhoneCall()
                }

                DetailViewModel.ZALO -> {
                    toZalo()
                }

                DetailViewModel.GOOGLE_MAP -> {
                    toGoogleMap()
                }

                DetailViewModel.BACK -> {
                    finish()
                }

                DetailViewModel.UPDATE_POST -> {
                    toUpdatePost()
                }
            }
        }
    }

    private fun toUpdatePost() {
        val intent = Intent(this, UpdatePostActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("post", detailViewModel.postDetail.value)
        intent.putExtras(bundle)
        launcher.launch(intent)
    }

    private fun toGoogleMap() {
        val latitude = detailViewModel.postDetail.value?.location?.latitude
        val longitude = detailViewModel.postDetail.value?.location?.longitude

        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)

    }

    private fun toPhoneCall() {
        val phoneNumber = detailViewModel.userDetail.value?.phoneNumber
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    private fun toZalo() {
        val phoneNumber = detailViewModel.userDetail.value?.phoneNumber
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://zalo.me/$phoneNumber")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Zalo chưa được cài đặt trên thiết bị!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}