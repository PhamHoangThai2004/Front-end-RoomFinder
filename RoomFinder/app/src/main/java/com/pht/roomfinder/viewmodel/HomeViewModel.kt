package com.pht.roomfinder.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.services.ListGroupData
import com.pht.roomfinder.services.PostService
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val keySearch = MutableLiveData<String>()
    val tmpSearch = MutableLiveData<String>()

    val status = MutableLiveData<Boolean>()
    val isNull = MutableLiveData<Boolean>()

    val listGroupData = MutableLiveData<List<ListGroupData>>()
    val listSearch = MutableLiveData<List<Post>>()

    private val postRepository = PostRepository(PostService.postService)

    fun popBack() {
        status.value = false
    }

    fun getListGroupData() {
        if (listGroupData.value == null) listGroupData()
    }

    fun search() {
        val keySearch = keySearch.value.toString().trim()
        if (keySearch.isNotEmpty()) getListSearch(keySearch)
    }

    private fun listGroupData() {

        viewModelScope.launch {
            val result = postRepository.getListGroup()

            if (result.isSuccess) {
                val postListResponse = result.getOrNull()
                postListResponse?.let {
                    listGroupData.value = it.data
                }
                Log.d("BBB", "Call api getlist thành công")
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "getListGroupData: ${error?.message}")
            }
        }
    }

    private fun getListSearch(keySearch: String) {
        viewModelScope.launch {
            val result = postRepository.getListSearch(keySearch)
            if (result.isSuccess) {
                val searchResponse = result.getOrNull()
                searchResponse?.let {
                    listSearch.value = it.data
                }
                Log.d("BBB", "Call api search thành công")
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "getListSearch: ${error?.message}")
            }
        }
    }

}