package com.pht.roomfinder.viewmodel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.R
import com.pht.roomfinder.helper.AuthState
import com.pht.roomfinder.helper.DataLocal
import com.pht.roomfinder.helper.Secure
import com.pht.roomfinder.model.Role
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.AuthRepository
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.utils.App
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.view.UserActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository(UserService.userService)

    @SuppressLint("StaticFieldLeak")
    private val context = App.getContext()!!

    val authState = MutableStateFlow(AuthState())
    val toLayout = MutableLiveData<Int>()
    val intentEvent = MutableLiveData<Intent?>()

    companion object {
        const val LOGIN = 0
        const val REGISTER = 1
        const val FORGOT_PASSWORD = 2
    }

    init {
        val isSave = DataLocal.getInstance().getBoolean(Const.SAVE_ACCOUNT)
        if (isSave) {
            val account = Secure.get(context)
            val email = account.first
            val password = account.second
            authState.value = authState.value.copy(email = email, password = password)
        }
    }

    fun toLogin() {
        toLayout.value = LOGIN
    }

    fun toRegister() {
        toLayout.value = REGISTER
    }

    fun toForgotPassword() {
        toLayout.value = FORGOT_PASSWORD
    }

    fun login() {
        val user = User(
            null, null, authState.value.email?.trim(),
            authState.value.password?.trim(), null, null, null, null, null
        )
        if (checkValid(user)) loginAccount(user)
    }

    fun register() {
        val user = User(
            null,
            Role(-1, "User"),
            authState.value.email?.trim(),
            authState.value.password?.trim(),
            authState.value.name?.trim(),
            null,
            authState.value.phone?.trim(),
            null,
            null
        )
        if (checkValid(user)) registerAccount(user)
    }

    private fun checkValid(user: User): Boolean {
        if (user.checkEmail()) authState.value = authState.value.copy(errEmail = null)
        else authState.value =
            authState.value.copy(errEmail = context.getString(R.string.invalid_email))

        if (user.checkPassword()) authState.value = authState.value.copy(errPassword = null)
        else authState.value =
            authState.value.copy(errPassword = context.getString(R.string.invalid_password))

        if (toLayout.value == REGISTER) {
            if (user.checkName()) authState.value = authState.value.copy(errName = null)
            else authState.value =
                authState.value.copy(errName = context.getString(R.string.invalid_name))

            if (user.checkPhoneNumber()) authState.value = authState.value.copy(errPhone = null)
            else authState.value =
                authState.value.copy(errPhone = context.getString(R.string.invalid_phone_number))
        }
        return authState.value.errEmail.isNullOrEmpty() && authState.value.errPassword.isNullOrEmpty()
                && authState.value.errName.isNullOrEmpty() && authState.value.errPhone.isNullOrEmpty()
    }

    private fun setLoading(isLoading: Boolean) {
        authState.value = authState.value.copy(isLoading = isLoading)
    }

    fun setOTPStatus(isOpenOTP: Boolean) {
        authState.value = authState.value.copy(isOpenOTP = isOpenOTP)
        authState.value = authState.value.copy(errOTP = null)
    }

    private fun loginAccount(user: User) {
        viewModelScope.launch {
            setLoading(true)
            val result = authRepository.login(user)
            setLoading(false)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        DataLocal.getInstance().putString(Const.TOKEN, it.token)
                        loginByToken(it.token)
                        Secure.encryptPassword(context, user.password!!)
                        authState.value = authState.value.copy(message = null)
                    } else {
                        authState.value =
                            authState.value.copy(message = context.getString(R.string.error_account))
                    }
                }
            } else {
                authState.value = authState.value.copy(message = context.getString(R.string.error))
            }

        }
    }

    private fun registerAccount(user: User) {
        viewModelScope.launch {
            setLoading(true)
            val result = authRepository.register(user)
            setLoading(false)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        setOTPStatus(true)
                        authState.value = authState.value.copy(message = null)
                    } else {
                        authState.value =
                            authState.value.copy(message = context.getString(R.string.email_used))
                    }
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "registerAccount: ${error?.message}")
                authState.value = authState.value.copy(message = context.getString(R.string.error))
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

    @Suppress("NAME_SHADOWING")
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
            val result = authRepository.checkOTP(authState.value.email.toString().trim(), otp)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        setOTPStatus(false)
                        DataLocal.getInstance().putString(Const.TOKEN, it.token)
                        loginByToken(it.token)
                    } else authState.value =
                        authState.value.copy(errOTP = context.getString(R.string.error_otp))
                }
            }
        }
    }

    fun resendOTP() {
        val user = User(
            -1,
            Role(-1, "User"),
            authState.value.email?.trim(),
            authState.value.password?.trim(),
            authState.value.name?.trim(),
            null,
            authState.value.phone?.trim(),
            null,
            null
        )
        registerAccount(user)
    }
}