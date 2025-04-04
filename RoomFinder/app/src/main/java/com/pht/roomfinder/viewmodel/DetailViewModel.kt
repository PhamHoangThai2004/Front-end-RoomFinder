package com.pht.roomfinder.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.R
import com.pht.roomfinder.helper.CloudinaryConfig
import com.pht.roomfinder.helper.UIState
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.services.PostService
import com.pht.roomfinder.utils.App
import com.pht.roomfinder.utils.Const
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class DetailViewModel : ViewModel() {
    private val postRepository = PostRepository(PostService.postService)
    private val context = App.getContext()!!

    val uiState = MutableStateFlow(UIState())

    private val _postDetail = MutableLiveData<Post>()
    val postDetail: LiveData<Post>
        get() = _postDetail

    private val _userDetail = MutableLiveData<User>()
    val userDetail: LiveData<User>
        get() = _userDetail

    private val _selectedPost = MutableLiveData<Int>()
    val selectedPost: LiveData<Int>
        get() = _selectedPost

    val listPost = MutableLiveData<List<Post>>()

    var totalImages = 0
    var totalPosts = MutableLiveData(0)
    val currentImage = MutableLiveData(0)
    var formatTime = MutableLiveData<String>()
    var createdAt = MutableLiveData<String>()
    var expireAt = MutableLiveData<String>()
    var date = MutableLiveData<String>()

    val toLayout = MutableLiveData<Int>()
    val isNull = MutableLiveData(false)
    val isShowBack = MutableLiveData(false)
    val isShowNext = MutableLiveData(true)

    companion object {
        const val DETAIL = 0
        const val CONTACT = 1
        const val CALL = 2
        const val ZALO = 3
        const val GOOGLE_MAP = 4
        const val BACK = 5
        const val UPDATE_POST = 6
    }

    fun selectPost(postID: Int) {
        _selectedPost.value = postID
    }

    fun getPostDetail(postID: Int) {
        viewModelScope.launch {
            setLoading(true)
            val result = postRepository.postDetail(postID)
            setLoading(false)
            if (result.isSuccess) {
                val postResponse = result.getOrNull()
                postResponse?.let {
                    if (it.status) _postDetail.value = it.data
                    else {
                        Toast.makeText(
                            App.getContext(),
                            context.getString(R.string.error),
                            Toast.LENGTH_SHORT
                        ).show()
                        back()
                    }
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "Error postDetail: ${error?.message}")
            }
        }
    }

    fun getUserDetail(userId: Int) {
        viewModelScope.launch {
            setLoading(true)
            val result = postRepository.userDetail(userId)
            setLoading(false)
            if (result.isSuccess) {
                val postResponse = result.getOrNull()
                postResponse?.let {
                    _userDetail.value = it.data.user
                    totalPosts.value = it.data.totalPosts
                    listPost.value = it.data.listPost
                    date.value = Const.formatDate(it.data.user.createdAt ?: "")
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "Error userDetail: ${error?.message}")
            }
        }
    }

    fun like() {
        viewModelScope.launch {
            val isLiked = postDetail.value!!.isLiked!!
            val result = postRepository.likePost(postDetail.value?.postID!!, isLiked)
            if (result.isSuccess) {
                val postResponse = result.getOrNull()
                postResponse?.let {
                    if (it.status) {
                        _postDetail.value = postDetail.value?.copy(
                            isLiked = !postDetail.value!!.isLiked!!,
                            tym = if (isLiked) postDetail.value!!.tym!! - 1 else postDetail.value!!.tym!! + 1
                        )
                    } else Toast.makeText(
                        context,
                        context.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "Error likePost: ${error?.message}")
            }
        }
    }

    fun openDialogConfirmDelete(isOpen: Boolean) {
        uiState.value = uiState.value.copy(isOpen = isOpen)
    }

    fun deleting() {
        openDialogConfirmDelete(false)
        viewModelScope.launch {
            val publicIds = mutableListOf<String>()
            postDetail.value?.images?.map {
                it.publicId?.let { it1 -> publicIds.add(it1) }
            }
            setLoading(true)
            if (publicIds.isNotEmpty()) {
                val result = CloudinaryConfig().deleteMultipleImages(publicIds)
                if (result) deletePost()
                else {
                    setLoading(false)
                    Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT)
                        .show()
                }
            } else deletePost()
        }
    }

    private suspend fun deletePost() {
        val result = postRepository.deletePost(postDetail.value?.postID!!)
        setLoading(false)
        if (result.isSuccess) {
            val postResponse = result.getOrNull()
            postResponse?.let {
                if (it.status) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.delete_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    back()
                } else Toast.makeText(
                    context,
                    context.getString(R.string.delete_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val error = result.exceptionOrNull()
            Log.d("BBB", "Error deletePost: ${error?.message}")
        }
    }

    fun backImage() {
        currentImage.value = currentImage.value?.minus(1)
        updateArrowStatus()
    }

    fun nextImage() {
        currentImage.value = currentImage.value?.plus(1)
        updateArrowStatus()
    }

    fun updateArrowStatus() {
        isShowBack.value = currentImage.value!! > 0
        isShowNext.value = currentImage.value!! < totalImages - 1
    }

    fun toContact() {
        toLayout(CONTACT)
    }

    fun toDetail() {
        toLayout(DETAIL)
    }

    fun toPhoneCall() {
        toLayout(CALL)
    }

    fun toZalo() {
        toLayout(ZALO)
    }

    fun toGoogleMap() {
        toLayout(GOOGLE_MAP)
    }

    fun back() {
        toLayout(BACK)
    }

    fun toUpdatePost() {
        toLayout(UPDATE_POST)
    }

    fun setValue() {
        createdAt.value = Const.formatDateTime(_postDetail.value?.createdAt.toString())
        formatTime.value = Const.changeTime(_postDetail.value?.createdAt.toString())
        expireAt.value = Const.formatDateTime(_postDetail.value?.expireAt.toString())
    }

    private fun toLayout(toLayout: Int) {
        this.toLayout.value = toLayout
    }

    fun setUserPosted(isPosted: Boolean) {
        uiState.value = uiState.value.copy(isUserPosted = isPosted)
    }

    private fun setLoading(isLoading: Boolean) {
        uiState.value = uiState.value.copy(isLoading = isLoading)
    }

}