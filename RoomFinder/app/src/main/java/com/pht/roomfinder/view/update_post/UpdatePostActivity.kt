package com.pht.roomfinder.view.update_post

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
import androidx.lifecycle.lifecycleScope
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.ActivityUpdatePostBinding
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.UpdatePostViewModel
import kotlinx.coroutines.launch
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

        lifecycleScope.launch {
            updatePostVM.postState.collect {
                if (it.isBack) finish()
            }
        }

        lifecycleScope.launch {
            updatePostVM.postState.collect {
                if (it.isSuccess) {
                    val intentUpdate = Intent()
                    intentUpdate.putExtra("post_id", updatePostVM.postId)
                    setResult(RESULT_OK, intentUpdate)
                    finish()
                }
            }
        }

        val dialogLoading = Const.setDialog(R.layout.dialog_loading, this)
        lifecycleScope.launch {
            updatePostVM.postState.collect {
                if (it.isLoading) dialogLoading.show()
                else dialogLoading.dismiss()
            }
        }
    }

    private fun setData(post: Post) {
        updatePostVM.postId = post.postID!!
        updatePostVM.userId = post.user!!.userId!!

        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.parse(post.expireAt, format)
        val now = LocalDateTime.now()

        updatePostVM.postState.value = updatePostVM.postState.value.copy(
            categoryName = post.category!!.categoryName,
            title = post.title,
            description = post.description,
            price = post.price.toString(),
            acreage = post.acreage.toString(),
            bonus = post.bonus,
            expireAt = "Không gia hạn",
            isExpired = date.isBefore(now)
        )
    }

    private fun setSpinner() {
        updatePostVM.getListCategories()
        updatePostVM.listCategories.observe(this) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bin.spinnerCategory.adapter = adapter

            val positionCategory =
                updatePostVM.listCategories.value!!.indexOf(updatePostVM.postState.value.categoryName)
            bin.spinnerCategory.setSelection(positionCategory)
        }

        bin.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updatePostVM.postState.value = updatePostVM.postState.value.copy(
                    categoryName = parent?.getItemAtPosition(position).toString()
                )
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
                updatePostVM.postState.value = updatePostVM.postState.value.copy(
                    expireAt = parent?.getItemAtPosition(position).toString()
                )
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
                        this@UpdatePostActivity.getString(R.string.error_title)
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
                    bin.inputLayoutDescription.error =
                        this@UpdatePostActivity.getString(R.string.error_description)
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
                    bin.inputLayoutPrice.error =
                        this@UpdatePostActivity.getString(R.string.error_price)
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
                    bin.inputLayoutAcreage.error =
                        this@UpdatePostActivity.getString(R.string.error_acreage)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        bin.editTextBonus.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.length > 200) {
                    bin.inputLayoutBonus.error =
                        this@UpdatePostActivity.getString(R.string.error_bonus)
                } else {
                    bin.inputLayoutBonus.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

}