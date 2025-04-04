package com.pht.roomfinder.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.helper.UIState
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.services.PostService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val postRepository = PostRepository(PostService.postService)

    val uiState = MutableStateFlow(UIState())

    private val _listPosts = MutableLiveData<List<Post>>()
    val listPosts: LiveData<List<Post>>
        get() = _listPosts

    private val _listExpired = MutableLiveData<List<Post>>()
    val listExpired: LiveData<List<Post>>
        get() = _listExpired

    private val _selectedPost = MutableLiveData<Int>()
    val selectedPost: LiveData<Int>
        get() = _selectedPost

    val listIsEmpty = MutableLiveData<Boolean>()

    fun setSelectedPost(postId: Int) {
        _selectedPost.value = postId
    }

    fun toNewPost(value: Boolean) {
        uiState.value = uiState.value.copy(isOpen = value)
    }

    fun getLists() {
        getListPosts()
        getListPosts(true)
    }

    private fun setLoading(isLoading: Boolean) {
        uiState.value = uiState.value.copy(isLoading = isLoading)
    }

    private fun getListPosts(isExpired: Boolean = false) {
        viewModelScope.launch {
            setLoading(true)
            val result = postRepository.listPost(isExpired)
            setLoading(false)
            if (result.isSuccess) {
                val response = result.getOrNull()
                response?.let {
                    if (isExpired) {
                        _listExpired.value = it.data
                    } else {
                        _listPosts.value = it.data

                    }
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "get your post: ${error?.message}")
            }
            if (listPosts.value?.isEmpty() == false || listExpired.value?.isEmpty() == false) listIsEmpty.value =
                false
            else listIsEmpty.value = true
        }
    }
}