package com.pht.roomfinder.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.AuthRepository
import kotlinx.coroutines.launch

class ForgotViewModel (private val authRepository: AuthRepository) : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val errorEmail = MutableLiveData<String>()
    val errorPassword = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val errorOTP = MutableLiveData<String>()

    val move = MutableLiveData<Int>()
    val dialogStatus = MutableLiveData<Boolean>()
    val otpStatus = MutableLiveData<Boolean>()

    init {
        move.value = 1
    }

    fun confirmEmail() {
        val user = User(null, null, email.value.toString().trim(), null, null, null, null)

        errorEmail.value = if (user.checkEmail()) { null }
        else { "Email không đúng định dạng" }

        if(errorEmail.value == null) {
            sendEmail(email.value.toString().trim())
        }
    }

    private fun sendEmail(email: String) {
        viewModelScope.launch {
            dialogStatus.value = true
            val result = authRepository.forgotPassword(email)
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
                errorMessage.value = "Có lỗi xảy ra"
            }
        }
    }

    fun checkOTP(otp: String) {
        viewModelScope.launch {
            val result = authRepository.confirmEmail(email.value.toString().trim(), otp)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        otpStatus.value = false
                        move.value = 2
                    } else errorOTP.value = it.message
                }
            }
        }
    }

    fun changePassword() {
        val user = User(null, null, email.value.toString().trim(),
            password.value.toString().trim(), null, null, null)

        errorPassword.value = if (user.checkPassword()) { null }
        else { "Mật khẩu phải có ít nhất 8 ký tự" }

        if(errorPassword.value == null) {
            createPassword(email.value.toString().trim(), password.value.toString().trim())
        }
    }

    private fun createPassword(email: String, newPassword: String) {
        viewModelScope.launch {
            val result = authRepository.createPassword(email, newPassword)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) { errorMessage.value = null
                    } else errorMessage.value = it.message
                }
            }
        }
    }

    fun resendOTP() {
        sendEmail(email.value.toString().trim())
    }

    fun move() {
        val a: Int = move.value ?: 0
        move.value = a - 1
    }

}

@Suppress("UNCHECKED_CAST")
class ForgotViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotViewModel::class.java)) {
            return ForgotViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}