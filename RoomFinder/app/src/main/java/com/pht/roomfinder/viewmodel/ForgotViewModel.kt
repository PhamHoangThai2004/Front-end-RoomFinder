package com.pht.roomfinder.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.R
import com.pht.roomfinder.helper.AuthState
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.AuthRepository
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.utils.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ForgotViewModel : ViewModel() {
    private val authRepository = AuthRepository(UserService.userService)

    @SuppressLint("StaticFieldLeak")
    private val context = App.getContext()

    companion object {
        const val LOGIN = 0
        const val CONFIRM_EMAIL = 1
        const val CREATE_PASSWORD = 2
    }

    val authState = MutableStateFlow(AuthState())
    val toLayout = MutableLiveData(CONFIRM_EMAIL)

    private fun setLoading(isLoading: Boolean) {
        authState.value = authState.value.copy(isLoading = isLoading)
    }

    fun toLayout(toLayout: Int) {
        this.toLayout.value = toLayout
    }

    fun popBackStack() {
        if (toLayout.value == CREATE_PASSWORD) toLayout(CONFIRM_EMAIL)
        else toLayout(LOGIN)
    }

    fun setOTPStatus(isOpenOTP: Boolean) {
        authState.value = authState.value.copy(isOpenOTP = isOpenOTP)
        authState.value = authState.value.copy(errOTP = null)
    }

    fun confirmEmail() {
        val email = authState.value.email?.trim() ?: ""
        val user = User(
            null, null, email, null,
            null, null, null, null, null
        )

        val error = if (user.checkEmail()) {
            null
        } else {
            App.getContext()!!.getString(R.string.invalid_email)
        }
        authState.value = authState.value.copy(errEmail = error)

        if (authState.value.errEmail == null) sendEmail(email)
    }

    private fun sendEmail(email: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = authRepository.forgotPassword(email)
            setLoading(false)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    var message: String? = context!!.getString(R.string.error_email)
                    if (it.status) {
                        setOTPStatus(true)
                        message = null
                    }
                    authState.value = authState.value.copy(message = message)
                }
            } else {
                authState.value =
                    authState.value.copy(message = App.getContext()!!.getString(R.string.error))
            }
        }
    }

    fun checkOTP(otp: String) {
        val email = authState.value.email?.trim()
        viewModelScope.launch {
            val result = authRepository.confirmEmail(email!!, otp)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        setOTPStatus(false)
                        toLayout(CREATE_PASSWORD)
                    } else authState.value =
                        authState.value.copy(errOTP = context!!.getString(R.string.error_otp))
                }
            }
        }
    }

    fun changePassword() {
        val email = authState.value.email!!.trim()
        val password = authState.value.password ?: ""
        val user = User(
            null, null, email,
            password, null, null, null, null, null
        )

        val error = if (user.checkPassword()) {
            null
        } else {
            context!!.getString(R.string.invalid_password)
        }

        authState.value = authState.value.copy(errPassword = error)

        if (authState.value.errPassword == null) {
            createPassword(email, password)
        }
    }

    private fun createPassword(email: String, newPassword: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = authRepository.createPassword(email, newPassword)
            setLoading(false)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        authState.value = authState.value.copy(isSuccess = true)
                    } else authState.value =
                        authState.value.copy(message = context!!.getString(R.string.error))
                }
            }
        }
    }

    fun resendOTP() {
        sendEmail(authState.value.email!!.trim())
    }

}