package com.pht.roomfinder.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.AuthRepository
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.utils.DataLocal
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    val user = MutableLiveData<User>()
    val name = MutableLiveData<String?>()
    val phoneNumber = MutableLiveData<String?>()
    val oldPassword = MutableLiveData<String?>()
    val newPassword = MutableLiveData<String?>()
    val confirmPassword = MutableLiveData<String?>()

    val move = MutableLiveData<Int>()
    val message = MutableLiveData<String?>()
    val isUpgrade = MutableLiveData<Boolean>()
    val isToast = MutableLiveData<Boolean>()
    val isShowBottomNavigation = MutableLiveData<Boolean>()

    private val authRepository = AuthRepository(UserService.userService)

    fun logout() {
        DataLocal.getInstance().putString(Const.TOKEN, "")
        saveAccount("", "", false)
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
            if (it.checkName() && it.checkPhoneNumber()) changeInformation(user)
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

    fun popBack() {
        move.value = 0
    }

    fun setUpgrade(b: Boolean) {
        isUpgrade.value = b
        name.value = user.value?.name
        phoneNumber.value = user.value?.phoneNumber
    }

    fun cancelChangePassword() {
        move.value = 0
        oldPassword.value = ""
        newPassword.value = ""
        confirmPassword.value = ""
    }

}