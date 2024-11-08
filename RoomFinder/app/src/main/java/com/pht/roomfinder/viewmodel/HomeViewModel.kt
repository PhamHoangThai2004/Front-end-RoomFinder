package com.pht.roomfinder.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.utils.App
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.services.ListGroupData
import com.pht.roomfinder.services.PostService
import com.pht.roomfinder.utils.DataLocal
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val listGroupData = MutableLiveData<List<ListGroupData>>()

    private val postRepository = PostRepository(PostService.postService)

    fun getListGroupData() {

        viewModelScope.launch {
            val result = postRepository.getListHome()

            if (result.isSuccess) {
                val postListResponse = result.getOrNull()
                postListResponse?.let {
                    listGroupData.value = it.data
                }
//                Log.d("BBB", "Call api thành công")
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "getListGroupData: ${error?.message}")
            }
        }
    }

}