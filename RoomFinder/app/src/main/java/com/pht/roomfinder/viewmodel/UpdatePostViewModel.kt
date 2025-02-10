package com.pht.roomfinder.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.Category
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.CategoryRepository
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.services.CategoryService
import com.pht.roomfinder.services.PostService
import com.pht.roomfinder.utils.App
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

class UpdatePostViewModel : ViewModel() {
    private val categoryRepository = CategoryRepository(CategoryService.categoryService)
    private val postRepository = PostRepository(PostService.postService)

    val listCategories = MutableLiveData<MutableList<String>>()

    var postId = -1
    var userId = -1
    val categoryName = MutableLiveData<String>()
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val price = MutableLiveData<String>()
    val acreage = MutableLiveData<String>()
    val bonus = MutableLiveData<String>()
    val expireAt = MutableLiveData("Không gia hạn")

    val isLoading = MutableLiveData(false)
    val isBack = MutableLiveData<Boolean>()
    val isExpired = MutableLiveData<Boolean>()


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
        isBack.value = true
    }

    private fun checkValid(): Boolean {
        if (title.value.isNullOrEmpty() || title.value!!.trim().length < 30 || title.value!!.trim().length > 100) return false
        if (description.value.isNullOrEmpty() || description.value!!.trim().length < 50 || description.value!!.trim().length > 3000) return false
        if (price.value.isNullOrEmpty()) return false
        if (acreage.value.isNullOrEmpty()) return false
        if ((bonus.value?.trim()?.length ?: 0) > 200) return false
        return true
    }

    fun getData() {
        isLoading.value = true
        if (!checkValid()) {
            Toast.makeText(App.getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        } else {
            val user = User(userId, null, null, null, null, null, null, null, null)
            val category = Category(null, categoryName.value)
            val expireTime = when (expireAt.value) {
                "3 ngày" -> 3
                "5 ngày" -> 5
                "7 ngày" -> 7
                else -> 0
            }
            val post = Post(postId, user, category, null, title.value!!.trim(),
                description.value!!.trim(), price.value?.toDouble(), acreage.value?.toDouble(),
                null, bonus.value?.trim() ?: "",null , expireTime.toString(), null, null, null)
            updatePost(post)
//            input(post)
        }
    }

    private fun updatePost(post: Post) {
        viewModelScope.launch {
            val result = postRepository.updatePost(post)
            isLoading.value = false
            if (result.isSuccess) {
                val response = result.getOrNull()
                response?.let {
                    if (it.status) {
                        Toast.makeText(App.getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        isBack.value = false
                    } else Toast.makeText(App.getContext(), "Sửa bài đăng thất bại", Toast.LENGTH_SHORT).show()
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "update post: ${error?.message}")
            }
        }
    }

    private fun input(post: Post) {
        Log.d("BBB", "postId: ${post.postID}")
        Log.d("BBB", "userId: ${post.user?.userId}")
        Log.d("BBB", "category: ${post.category?.categoryName}")
        Log.d("BBB", "title: ${post.title}")
        Log.d("BBB", "description: ${post.description}")
        Log.d("BBB", "price: ${post.price}")
        Log.d("BBB", "acreage: ${post.acreage}")
        Log.d("BBB", "bonus: ${post.bonus}")
        Log.d("BBB", "expireAt: ${post.expireAt}")
    }
}