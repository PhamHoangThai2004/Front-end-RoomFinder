package com.pht.roomfinder.user.update_post

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.ActivityUpdatePostBinding
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.UpdatePostViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UpdatePostActivity : AppCompatActivity() {
    private lateinit var bin: ActivityUpdatePostBinding
    private lateinit var updatePostVM: UpdatePostViewModel

    @SuppressLint("SourceLockedOrientationActivity", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        bin = ActivityUpdatePostBinding.inflate(layoutInflater)
        setContentView(bin.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updatePostVM = ViewModelProvider(this)[UpdatePostViewModel::class.java]
        bin.updatePostVM = updatePostVM

        val intent = intent
        val post = intent.getSerializableExtra("post", Post::class.java)

        setInput()

        setSpinner()

        if (post != null) setData(post)
        else finish()

        updatePostVM.isBack.observe(this) {
            if (it) finish()
            else {
                val intentUpdate = Intent()
                intentUpdate.putExtra("post_id", updatePostVM.postId)
                setResult(RESULT_OK, intentUpdate)
                finish()
            }
        }

        val dialogLoading = Const.setDialog(R.layout.dialog_loading, this)
        updatePostVM.isLoading.observe(this) {
            if (it) dialogLoading.show()
            else dialogLoading.dismiss()
        }
    }

    private fun setData(post: Post) {
        updatePostVM.postId = post.postID!!
        updatePostVM.userId = post.user!!.userId!!

        updatePostVM.categoryName.value = post.category!!.categoryName
        updatePostVM.title.value = post.title
        updatePostVM.description.value = post.description
        updatePostVM.price.value = post.price.toString()
        updatePostVM.acreage.value = post.acreage.toString()
        updatePostVM.bonus.value = post.bonus

        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.parse(post.expireAt, format)
        val now = LocalDateTime.now()
        updatePostVM.isExpired.value = date.isBefore(now)
    }

    private fun setSpinner() {
        updatePostVM.getListCategories()
        updatePostVM.listCategories.observe(this) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bin.spinnerCategory.adapter = adapter

            val positionCategory =
                updatePostVM.listCategories.value!!.indexOf(updatePostVM.categoryName.value)
            bin.spinnerCategory.setSelection(positionCategory)
        }

        bin.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updatePostVM.categoryName.value = updatePostVM.listCategories.value!![position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        bin.spinnerExpireTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updatePostVM.expireAt.value = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun setInput() {
        bin.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.length < 30 || text.length > 100) {
                    bin.inputLayoutTitle.error =
                        "Tiêu đề phải tối thiểu 30 ký tự và tối đa 100 ký tự"
                } else {
                    bin.inputLayoutTitle.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        bin.editTextDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.length < 50 || text.length > 3000) {
                    bin.inputLayoutDescription.error = "Tối thiểu 50 ký tự và tối đa 3000 ký tự"
                } else {
                    bin.inputLayoutDescription.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        bin.editTextPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.isNotEmpty()) {
                    bin.inputLayoutPrice.error = null
                } else {
                    bin.inputLayoutPrice.error = "Yêu cầu nhập giá cho thuê"
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        bin.editTextAcreage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.isNotEmpty()) {
                    bin.inputLayoutAcreage.error = null
                } else {
                    bin.inputLayoutAcreage.error = "Yêu cầu nhập diện tích"
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        bin.editTextBonus.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.length > 200) {
                    bin.inputLayoutBonus.error = "Tối đa 200 ký tự"
                } else {
                    bin.inputLayoutBonus.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

}