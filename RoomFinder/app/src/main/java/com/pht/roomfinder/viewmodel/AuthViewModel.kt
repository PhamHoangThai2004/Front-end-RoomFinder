package com.pht.roomfinder.viewmodel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.Role
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.AuthRepository
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.user.UserActivity
import com.pht.roomfinder.utils.App
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.utils.DataLocal
import kotlinx.coroutines.launch

@Suppress("NAME_SHADOWING")
class AuthViewModel() : ViewModel() {
    val errorEmail = MutableLiveData<String>()
    val errorPassword = MutableLiveData<String>()
    val errorName = MutableLiveData<String>()
    val errorPhone = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String?>()

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val name = MutableLiveData<String>()

    val move = MutableLiveData<Int>()
    val intentEvent = MutableLiveData<Intent?>()
    val otpStatus = MutableLiveData<Boolean>()
    val errorOTP = MutableLiveData<String>()
    val dialogStatus = MutableLiveData<Boolean>()

    init {
        email.value = DataLocal.getInstance().getString(Const.EMAIL)
        password.value = DataLocal.getInstance().getString(Const.PASSWORD)
    }

    private val authRepository = AuthRepository(UserService.userService)

    fun moveLogin() {
        move.value = 0
    }

    fun moveRegister() {
        move.value = 1
    }

    fun moveForgotPassword() {
        move.value = 3
    }

    fun login() {
        val user = User(
            null, null, email.value.toString().trim(),
            password.value.toString().trim(), null, null, null, null, null
        )

        if (checkValid(user)) loginAccount(user)
    }

    fun register() {
        // Bị lỗi đăng ký, cần fix lại sau
        val user = User(
            null, Role(-1, "User"), email.value.toString().trim(), password.value.toString().trim(),
            name.value.toString().trim(), null , phoneNumber.value.toString().trim(), null, null
        )

        if (checkValid(user)) registerAccount(user)
    }

    private fun checkValid(user: User): Boolean {
        errorEmail.value = if (user.checkEmail()) {
            null
        } else {
            "Email không đúng định dạng"
        }

        errorPassword.value = if (user.checkPassword()) {
            null
        } else {
            "Mật khẩu phải có tối thiểu 8 ký tự"
        }

        if (move.value == 1) {
            errorPhone.value = if (user.checkPhoneNumber()) {
                null
            } else {
                "Số điện thoại không đúng định dạng"
            }

            errorName.value = if (user.checkName()) {
                null
            } else {
                "Tên phải có ít nhất 2 ký tự"
            }
        }
        return errorEmail.value.isNullOrEmpty() && errorPassword.value.isNullOrEmpty()
                && errorPhone.value.isNullOrEmpty() && errorName.value.isNullOrEmpty()
    }

    private fun loginAccount(user: User) {
        viewModelScope.launch {
            dialogStatus.value = true
            val result = authRepository.login(user)
            dialogStatus.value = false
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        DataLocal.getInstance().putString(Const.TOKEN, it.token)
                        loginByToken(it.token)
                        DataLocal.getInstance()
                            .putString(Const.PASSWORD, password.value.toString().trim())
                        errorMessage.value = null
                    } else {
                        errorMessage.value = it.message
                    }
                }
            } else {
                val error = result.exceptionOrNull()
                errorMessage.value = "Có lỗi xảy ra"
            }

        }
    }

    private fun registerAccount(user: User) {
        viewModelScope.launch {
            dialogStatus.value = true
            val result = authRepository.register(user)
            dialogStatus.value = false

            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        otpStatus.value = true
                        errorMessage.value = null
                    } else {
                        errorMessage.value = it.message
                    }
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "registerAccount: ${error?.message}")
                errorMessage.value = "Có lỗi xảy ra"
            }
        }
    }

    fun loginByToken(token: String) {
        viewModelScope.launch {
            val result = authRepository.checkToken(token)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) checkRole(it.data.user)
                }
            } else intentEvent.postValue(null)
        }
    }

    private fun checkRole(user: User) {
        if (user.role?.roleName == "User") {
            val intent = Intent(App.getContext(), UserActivity::class.java)
            val user = User(
                user.userId, user.role, user.email,
                null, user.name, user.avatar, user.phoneNumber, user.address, null
            )
            val bundle = Bundle()
            bundle.putSerializable("user", user)
            intent.putExtras(bundle)

            intentEvent.postValue(intent)
        }
    }

    fun checkOTP(otp: String) {
        viewModelScope.launch {
            val result = authRepository.checkOTP(email.value.toString().trim(), otp)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        DataLocal.getInstance().putString(Const.TOKEN, it.token)
                        loginByToken(it.token)
                    } else errorOTP.value = it.message
                }
            }
        }
    }

    fun resendOTP() {
        val user = User(
            -1, Role(-1, "User"), email.value.toString().trim(), password.value.toString().trim(),
            name.value.toString().trim(), phoneNumber.value.toString().trim(), null, null, null
        )
        registerAccount(user)
    }
}