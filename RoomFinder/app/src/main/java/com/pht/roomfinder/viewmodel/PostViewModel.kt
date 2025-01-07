package com.pht.roomfinder.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.Category
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.repositories.CategoryRepository
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.services.CategoryService
import com.pht.roomfinder.services.PostService
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val _selectedPost = MutableLiveData<Int>()
    val selectedPost: LiveData<Int>
        get() = _selectedPost

    val listCategory = MutableLiveData<MutableList<String>>()
    val listPost = MutableLiveData<List<Post>>()

    val isNull = MutableLiveData<Boolean>()
    val isOpenFilter = MutableLiveData<Boolean>()

    val category = MutableLiveData<String>()
    val province = MutableLiveData<String>()
    val price = MutableLiveData<String>()
    val acreage = MutableLiveData<String>()

    init {
        category.value = "Tất cả"
        province.value = "Toàn quốc"
        price.value = "Tất cả"
        acreage.value = "Tất cả"
    }

    fun selectPost(postID: Int) {
        _selectedPost.value = postID
    }

    private val categoryRepository = CategoryRepository(CategoryService.categoryService)
    private val postRepository = PostRepository(PostService.postService)

    fun getListCategory() {
        viewModelScope.launch {
            val result = categoryRepository.listCategory()
            if (result.isSuccess) {
                val response = result.getOrNull()
                var list = listOf<Category>()
                response?.let {
                    list = it.data
                }
                listCategory.value = mutableListOf("Tất cả")
                for (item in list) {
                    listCategory.value?.add(item.categoryName!!)
                }
            } else {
                isNull.value = true
            }
        }
    }

    fun getFilterValue() {
        val valuePrices = filterNumber(price.value!!)
        val valueAcreage = filterNumber(acreage.value!!)
        getPostFilter(
            category.value!!,
            province.value!!,
            valuePrices[0],
            valuePrices[1],
            valueAcreage[0],
            valueAcreage[1]
        )
    }

    fun openFilter(isOpen: Boolean) {
        isOpenFilter.value = isOpen
    }

    private fun getPostFilter(
        categoryName: String,
        area: String,
        minPrice: Int,
        maxPrice: Int,
        minAcreage: Int,
        maxAcreage: Int
    ) {
        viewModelScope.launch {
            val result = postRepository.postFilter(
                categoryName,
                area,
                minPrice,
                maxPrice,
                minAcreage,
                maxAcreage
            )
            if (result.isSuccess) {
                val searchResponse = result.getOrNull()
                searchResponse?.let {
                    listPost.value = it.data
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "get list filter: ${error?.message}")
            }

        }
    }

    private fun filterNumber(value: String): List<Int> {
        if (value == "Tất cả") {
            val list = listOf(-1, -1)
            return list
        }

        if (value.indexOf("Dưới") != -1) {
            val list = listOf(value.replace(Regex("[^\\d.]"), "").toInt(), -1)
            return list
        }

        if (value.indexOf("Trên") != -1) {
            val list = listOf(-1, value.replace(Regex("[^\\d.]"), "").toInt())
            return list
        }

        val parts = value.split("-").map { it.trim() }
        val list = listOf(
            parts[0].replace(Regex("[^\\d.]"), "").toInt(),
            parts[1].replace(Regex("[^\\d.]"), "").toInt()
        )

        return list
    }

}