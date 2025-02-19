package com.pht.roomfinder.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.AuthRepository
import com.pht.roomfinder.repositories.PostRepository
import com.pht.roomfinder.services.PostService
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.user.setting.SettingFragment
import com.pht.roomfinder.utils.App
import com.pht.roomfinder.utils.CloudinaryConfig
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.utils.DataLocal
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val authRepository = AuthRepository(UserService.userService)
    private val postRepository = PostRepository(PostService.postService)

    private val _listPosts = MutableLiveData<List<Post>>()
    val listPosts: LiveData<List<Post>>
        get() = _listPosts

    private val _listExpired = MutableLiveData<List<Post>>()
    val listExpired: LiveData<List<Post>>
        get() = _listExpired

    val selectedPost = SingLiveData<Int>()

    val user = MutableLiveData<User>()
    val name = MutableLiveData<String?>()
    val phoneNumber = MutableLiveData<String?>()
    val address = MutableLiveData<String?>()
    val avatar = MutableLiveData<String?>()
    val oldPassword = MutableLiveData<String?>()
    val newPassword = MutableLiveData<String?>()
    val confirmPassword = MutableLiveData<String?>()
    val imagePath = MutableLiveData<String>()

    val move = MutableLiveData<Int>()
    val message = MutableLiveData<String?>()
    val isUpgrade = MutableLiveData<Boolean>()
    val isToast = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData(false)
    val listIsNull = MutableLiveData<Boolean>()
    val expiredIsNull = MutableLiveData<Boolean>()
    val isShowBottomNavigation = MutableLiveData<Boolean>()

    fun logout() {
        DataLocal.getInstance().clear()
    }

    fun saveAccount(email: String, password: String, isSave: Boolean) {
        if (isSave) {
            DataLocal.getInstance().putString(Const.EMAIL, email)
            DataLocal.getInstance().putString(Const.PASSWORD, password)
        } else {
            DataLocal.getInstance().putString(Const.EMAIL, "")
            DataLocal.getInstance().putString(Const.PASSWORD, "")
        }
        DataLocal.getInstance().putBoolean(Const.SAVE_ACCOUNT, isSave)
    }

    fun checkInformation() {
        val user = user.value
        user?.let {
            it.name = name.value.toString().trim()
            it.phoneNumber = phoneNumber.value.toString().trim()
            it.address = address.value.toString().trim()
            if (it.checkName() && it.checkPhoneNumber() && it.checkAddress()) changeInformation(user)
            else {
                message.value = "Thông tin chỉnh sửa không hợp lệ"
                isToast.value = true
            }
        }
        isToast.value = false
    }

    fun checkPassword() {
        if (oldPassword.value.toString().trim().isEmpty() || newPassword.value.toString().trim()
                .isEmpty() || confirmPassword.value.toString().trim().isEmpty()
        ) {
            message.value = "Yêu cầu nhập đầy đủ thông tin"
            isToast.value = true
        } else {
            if (newPassword.value.toString().trim().length < 8) {
                message.value = "Mật khẩu mới phải có tối thiểu 8 ký tự"
                isToast.value = true
            } else if (newPassword.value.toString().trim() != confirmPassword.value.toString()
                    .trim()
            ) {
                message.value = "Xác nhận mật khẩu không trùng khớp"
                isToast.value = true
            } else changePassword(
                oldPassword.value.toString().trim(),
                newPassword.value.toString().trim()
            )
        }
        isToast.value = false
    }

    fun getList() {
        getListPosts()
        getListPosts(true)
    }

    fun uploadImage() {
        viewModelScope.launch {
            if (!imagePath.value.isNullOrEmpty()) {
                isLoading.value = true

                if (avatar.value != null) {
                    val image = avatar.value!!.substringAfterLast("/")
                    val publicId = image.substringBeforeLast(".")
                    val result = CloudinaryConfig().deleteImage(publicId)
                    if (result) {
                        val imageUrl = CloudinaryConfig().uploadImage(imagePath.value!!)
                        changeAvatar(imageUrl)
                    } else {
                        isLoading.value = false
                        setUpgrade(false)
                        Toast.makeText(App.getContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val imageUrl = CloudinaryConfig().uploadImage(imagePath.value!!)
                    changeAvatar(imageUrl)
                }
            } else {
                isUpgrade.value = false
                Toast.makeText(App.getContext(), "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun changeAvatar(imageUrl: String) {
        if (imageUrl == "") {
            Log.d("BBB", "Có lỗi xảy ra")
            setUpgrade(false)
            isLoading.value = false
        } else {
            val user = user.value
            user?.avatar = imageUrl
            val result = authRepository.changeAvatar(imageUrl)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        Toast.makeText(
                            App.getContext(),
                            "Đổi ảnh đại diện thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        this@UserViewModel.user.value = user
                    } else {
                        Toast.makeText(
                            App.getContext(),
                            "Thay ảnh đại diện thất bại",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "change avatar: ${error?.message}")
            }
            isLoading.value = false
            setUpgrade(false)
        }
    }

    private fun getListPosts(isExpired: Boolean = false) {
        viewModelScope.launch {
            val result = postRepository.listPost(isExpired)
            if (result.isSuccess) {
                val response = result.getOrNull()
                response?.let {
                    if (isExpired) {
                        _listExpired.value = it.data
                        expiredIsNull.value = it.data.isEmpty()
                    } else {
                        _listPosts.value = it.data
                        listIsNull.value = it.data.isEmpty()
                    }
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "get your post: ${error?.message}")
            }
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        val token = "Bearer ${DataLocal.getInstance().getString(Const.TOKEN)}"
        viewModelScope.launch {
            val result = authRepository.changePassword(token, oldPassword, newPassword)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        message.value = it.message
                        DataLocal.getInstance().putString(Const.TOKEN, "")
                        DataLocal.getInstance().putString(Const.PASSWORD, newPassword)
                    } else message.value = it.message
                    isToast.value = true
                }
            } else {
                val error = result.exceptionOrNull()
                message.value = error?.message
                isToast.value = true
            }
            isToast.value = false
        }
    }

    private fun changeInformation(user: User) {
        val token = "Bearer ${DataLocal.getInstance().getString(Const.TOKEN)}"
        viewModelScope.launch {
            val result = authRepository.changeInformation(token, user)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        message.value = it.message
                        DataLocal.getInstance().putString(Const.TOKEN, it.token)
                        this@UserViewModel.user.value = user
                    } else message.value = it.message
                    isToast.value = true
                }
            } else {
                val error = result.exceptionOrNull()
                isToast.value = true
                message.value = error?.message
            }
            isToast.value = false
            isUpgrade.value = false
        }
    }

    fun toNewPost() {
        move.value = SettingFragment.NEW_POST
    }

    fun popBack() {
        move.value = SettingFragment.OPTION
    }

    fun toFunction(value: Int) {
        move.value = value
        isShowBottomNavigation.value = false
    }

    fun setUpgrade(value: Boolean) {
        isUpgrade.value = value
        if (!value) {
            name.value = user.value?.name
            phoneNumber.value = user.value?.phoneNumber
            address.value = user.value?.address
            avatar.value = user.value?.avatar
        }
    }

    fun cancelChangePassword() {
        popBack()
        oldPassword.value = ""
        newPassword.value = ""
        confirmPassword.value = ""
    }

}