package com.pht.roomfinder.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.model.Role
import com.pht.roomfinder.model.User
import com.pht.roomfinder.repositories.AuthRepository
import com.pht.roomfinder.services.UserService
import com.pht.roomfinder.user.UserActivity
import kotlinx.coroutines.launch

@Suppress("NAME_SHADOWING")
class AuthViewModel(application: Application, private val authRepository: AuthRepository) : AndroidViewModel(application) {
    val errorEmail = MutableLiveData<String>()
    val errorPassword = MutableLiveData<String>()
    val errorName = MutableLiveData<String>()
    val errorPhone = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()

//    val showError = MutableLiveData<Boolean>()

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val name = MutableLiveData<String>()

    val move = MutableLiveData<Int>()
    val intentEvent = MutableLiveData<Intent>()
    val otpStatus = MutableLiveData<Boolean>()
    val errorOTP = MutableLiveData<String>()
    val dialogStatus = MutableLiveData<Boolean>()

    private val sharedPreferences = application.getSharedPreferences("data", Application.MODE_PRIVATE)

    fun moveLogin() { move.value = 0 }

    fun moveRegister() { move.value = 1 }

    fun moveForgotPassword() { move.value = 3 }

    fun login() {
        val user = User(null, null, email.value.toString().trim() ,
            password.value.toString().trim() , null, null, null)

        if (checkValid(user)) loginAccount(user)
    }

    fun register() {
        val user = User(null, Role(-1, "User"), email.value.toString().trim() , password.value.toString().trim(),
            name.value.toString().trim(), phoneNumber.value.toString().trim(), null)

        if (checkValid(user)) registerAccount(user)
    }

    private fun checkValid(user: User): Boolean {
        errorEmail.value = if (user.checkEmail()) { null }
        else { "Email không đúng định dạng" }

        errorPassword.value = if (user.checkPassword()) { null }
        else { "Mật khẩu phải có tối thiểu 8 ký tự" }

        if(move.value == 1) {
            errorPhone.value = if (user.checkPhoneNumber()) { null }
            else { "Số điện thoại không đúng định dạng" }

            errorName.value = if (user.checkName()) { null }
            else { "Tên phải có ít nhất 2 ký tự" }
        }
        return errorEmail.value.isNullOrEmpty() && errorPassword.value.isNullOrEmpty()
                && errorPhone.value.isNullOrEmpty() && errorName.value.isNullOrEmpty()
    }

    private fun loginAccount(user: User) {
        viewModelScope.launch {
            val result = authRepository.login(user)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
               authResponse?.let {
                   if (it.status) {
                       sharedPreferences.edit().putString("token", it.token).apply()
                       directLogin(it.token)
                   }
                   else {
                       errorMessage.value = it.message
                   }
               }
            } else {
                val error = result.exceptionOrNull()
                Log.d("BBB", "loginAccount: ${error?.message}")
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

    fun directLogin(token: String) {
        viewModelScope.launch {
            val result = authRepository.checkToken(token)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()
                authResponse?.let {
                    if (it.status) {
                        checkRole(it.data.user)
                    }
                }
            }
        }
    }

    private fun checkRole(user: User) {
        if(user.role?.roleName == "User") {
            val intent = Intent(getApplication(), UserActivity::class.java)
            val user = User(user.userId, user.role, user.email,
                null, user.name, user.phoneNumber, null)
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
                        sharedPreferences.edit().putString("token", it.token).apply()
                        directLogin(it.token)
                    } else errorOTP.value = it.message
                }
            }
        }
    }

    fun resendOTP() {
        val user = User(-1, Role(-1, "User"), email.value.toString().trim(), password.value.toString().trim(),
            name.value.toString().trim(), phoneNumber.value.toString().trim(), null)
        registerAccount(user)
    }
}

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val application: Application, private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(application, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}