package com.pht.roomfinder.viewmodel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.R
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
@SuppressLint("StaticFieldLeak")
class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository(UserService.userService)
    val context = App.getContext()!!

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
        val isSave = DataLocal.getInstance().getBoolean(Const.SAVE_ACCOUNT)
        if (isSave) {
            email.value = DataLocal.getInstance().getString(Const.EMAIL)
            password.value = DataLocal.getInstance().getString(Const.PASSWORD)
        }
    }

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
        val user = User(
            null, Role(-1, "User"), email.value.toString().trim(), password.value.toString().trim(),
            name.value.toString().trim(), null, phoneNumber.value.toString().trim(), null, null
        )
        if (checkValid(user)) registerAccount(user)
    }

    private fun checkValid(user: User): Boolean {
        errorEmail.value = if (user.checkEmail()) {
            null
        } else {
            context.getString(R.string.invalid_email)
        }

        errorPassword.value = if (user.checkPassword()) {
            null
        } else {
            context.getString(R.string.invalid_password)
        }

        if (move.value == 1) {
            errorPhone.value = if (user.checkPhoneNumber()) {
                null
            } else {
                context.getString(R.string.invalid_phone_number)
            }

            errorName.value = if (user.checkName()) {
                null
            } else {
                context.getString(R.string.invalid_name)
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
                        errorMessage.value = context.getString(R.string.error_account)
                    }
                }
            } else {
                errorMessage.value = context.getString(R.string.error)
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
                        errorMessage.value = context.getString(R.string.email_used)
                    }
                }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "registerAccount: ${error?.message}")
                errorMessage.value = context.getString(R.string.error)
            }
        }
    }

    fun loginByToken(token: String) {
        viewModelScope.launch {
            val result = authRepository.checkToken(token)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        checkRole(it.data.user)
                    }
                }
            } else {
                intentEvent.postValue(null)
            }
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
                        otpStatus.value = false
                        DataLocal.getInstance().putString(Const.TOKEN, it.token)
                        loginByToken(it.token)
                    } else errorOTP.value = context.getString(R.string.error_otp)
                }
            }
        }
    }

    fun resendOTP() {
        val user = User(
            -1, Role(-1, "User"), email.value.toString().trim(), password.value.toString().trim(),
            name.value.toString().trim(), null, phoneNumber.value.toString().trim(), null, null
        )
        registerAccount(user)
    }
}