package com.pht.roomfinder.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.R
import com.pht.roomfinder.helper.PostState
import com.pht.roomfinder.model.Category
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.CategoryRepository
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.services.CategoryService
import com.pht.roomfinder.services.PostService
import com.pht.roomfinder.utils.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class UpdatePostViewModel : ViewModel() {
    private val categoryRepository = CategoryRepository(CategoryService.categoryService)
    private val postRepository = PostRepository(PostService.postService)
    private val context = App.getContext()!!

    val postState = MutableStateFlow(PostState())

    val listCategories = MutableLiveData<MutableList<String>>()

    var postId = -1
    var userId = -1

    private fun setLoading(isLoading: Boolean) {
        postState.value = postState.value.copy(isLoading = isLoading)
    }

    fun getListCategories() {
        viewModelScope.launch {
            val result = categoryRepository.listCategory()
            if (result.isSuccess) {
                val response = result.getOrNull()
                val list = mutableListOf<String>()
                response?.let {
                    for (item in response.data) {
                        list.add(item.categoryName!!)
                    }
                }
                listCategories.value = list
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "get list category: ${error?.message}")
            }
        }
    }

    fun back() {
        postState.value = postState.value.copy(isBack = true)
    }

    private fun checkValid(
        title: String?, description: String?, price: String?,
        acreage: String?, bonus: String?
    ): Boolean {
        if (title.isNullOrEmpty() || title.trim().length < 30 || title.trim().length > 100) return false
        if (description.isNullOrEmpty() || description.trim().length < 50 || description.trim().length > 3000) return false
        if (price.isNullOrEmpty()) return false
        if (acreage.isNullOrEmpty()) return false
        if ((bonus?.trim()?.length ?: 0) > 200) return false
        return true
    }

    fun getData() {
        val title = postState.value.title
        val description = postState.value.description
        val price = postState.value.price
        val acreage = postState.value.acreage
        val bonus = postState.value.bonus

        if (!checkValid(title, description, price, acreage, bonus)) {
            Toast.makeText(
                context,
                context.getString(R.string.full_information_required),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val user = User(userId, null, null, null, null, null, null, null, null)
            val category = Category(null, postState.value.categoryName)
            val expireTime = when (postState.value.expireAt) {
                "3 ngày" -> 3
                "5 ngày" -> 5
                "7 ngày" -> 7
                else -> 0
            }
            val post = Post(
                postId, user, category, null, title?.trim(),
                description?.trim(), price?.toDouble(), acreage?.toDouble(),
                null, bonus?.trim() ?: "", null,
                expireTime.toString(), null, null, null
            )
            updatePost(post)
        }
    }

    private fun updatePost(post: Post) {
        viewModelScope.launch {
            setLoading(true)
            val result = postRepository.updatePost(post)
            setLoading(false)
            if (result.isSuccess) {
                val response = result.getOrNull()
                response?.let {
                    if (it.status) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.edit_post_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        postState.value = postState.value.copy(isSuccess = true)
                    } else Toast.makeText(
                        context,
                        context.getString(R.string.edit_post_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "update post: ${error?.message}")
            }
        }
    }
}