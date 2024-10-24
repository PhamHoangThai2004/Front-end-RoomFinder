package com.pht.roomfinder

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.api.AccountService
import com.pht.roomfinder.objects.User
import com.pht.roomfinder.users.home.HomeActivity
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val errorEmail = MutableLiveData<String>()
    val errorPassword = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val errorRepeat = MutableLiveData<String>()

    val showError = MutableLiveData<Boolean>()

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeatPassword = MutableLiveData<String>()

    val moveFragment = MutableLiveData<Boolean>()
    val intentEvent = MutableLiveData<Intent>()
    val otpMessage = MutableLiveData<String>()

    private val sharedPreferences = application.getSharedPreferences("data", Application.MODE_PRIVATE)

    fun moveLogin() {
        moveFragment.value = true
    }

    fun moveRegister() {
        moveFragment.value = false
    }

    fun login() {
        val user = User(-1, -1, email.value.toString().trim(), password.value.toString().trim(), "", "")

        if (checkValid(user)) {
            loginAccount(user)
        }
    }

    fun register() {
        val user = User(-1, 1, email.value.toString().trim(), password.value.toString().trim(), "", "")

        if (checkValid(user)) {
            registerAccount(user)
        }
    }

    private fun checkValid(account: User): Boolean {
        errorEmail.value = if (account.checkEmail()) {
            null
        } else {
            "Email không đúng định dạng"
        }

        errorPassword.value = if (account.checkPassword()) {
            null
        } else {
            "Mật khẩu phải có tối thiểu 8 ký tự"
        }

        if(moveFragment.value == false) {
            errorRepeat.value = if (account.checkRepeatPassword(repeatPassword.value.toString().trim())) {
                null
            } else {
                "Mật khẩu nhắc lại phải trùng với mật khẩu đã nhập"
            }
        }

        return errorEmail.value.isNullOrEmpty() && errorPassword.value.isNullOrEmpty() && errorRepeat.value.isNullOrEmpty()
    }

    private fun registerAccount(user: User) {
        viewModelScope.launch {
            try {
//                val response = AccountService.accountService.register(user)
                otpMessage.value = "Đã gửi OTP đến email"
//                if(response.status) {
//                    showError.value = true
//                    otpMessage.value = response.message
//                    Log.d("BBB", otpMessage.value.toString())
////                    sharedPreferences.edit().putString("token", response.token).apply()
////                    directLogin()
//                } else {
//                    showError.value = true
//                    errorMessage.value = response.message
//                }
            } catch (e: Exception) {
                showError.value = true
                errorMessage.value = "Không thể gửi email"
            }
        }
    }

    private fun loginAccount(user: User) {
        viewModelScope.launch {
            try {
                val response = AccountService.accountService.login(user)

                if (response.status) {
                    showError.value = false
                    sharedPreferences.edit().putString("token", response.token).apply()
                    errorMessage.value = response.message
                    directLogin()
                } else {
                    showError.value = true
                    errorMessage.value = response.message
                }
            } catch (e : Exception) {
                showError.value = true
                errorMessage.value = e.message
            }
        }
    }

    fun directLogin() {
        val token: String? = sharedPreferences.getString("token", null)
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = AccountService.accountService.checkToken(token)
                    if (response.status) {
                        if(response.data.roleName == "User") {
                            val intent = Intent(getApplication(), HomeActivity::class.java)

                            val user = User(response.data.userID, 1, response.data.email,
                                "", response.data.name?: "", response.data.phoneNumber?: "")
                            val bundle = Bundle()
                            bundle.putSerializable("user", user)
                            intent.putExtras(bundle)

                            intentEvent.postValue(intent)
                        }
                    }
                } catch (e: Exception) { println(e.message) }
            }
        }
    }

}