package com.pht.roomfinder.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.services.PostService
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {
    private val postRepository = PostRepository(PostService.postService)

    private val _listFavorite = MutableLiveData<List<Post>>()
    val listFavorite: LiveData<List<Post>>
        get() = _listFavorite

    private val _selectedPost = MutableLiveData<Int>()
    val selectedPost: LiveData<Int>
        get() = _selectedPost

    val isLoading = MutableLiveData(false)

    fun selectPost(postID: Int) {
        _selectedPost.value = postID
    }

    fun getFavoritePosts() {
        viewModelScope.launch {
            isLoading.value = true
            val result = postRepository.favoritePost()
            isLoading.value = false
            if (result.isSuccess) {
                val response = result.getOrNull()
                response?.let {
                    _listFavorite.value = it.data
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "get favorite post: ${error?.message}")
            }
        }
    }

}