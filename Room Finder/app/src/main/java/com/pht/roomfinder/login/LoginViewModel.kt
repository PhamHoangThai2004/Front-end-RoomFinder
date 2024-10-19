package com.pht.roomfinder.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.api.LoginService
import com.pht.roomfinder.objects.Account
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val errorUsername = MutableLiveData<String>()
    val errorPassword = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val showError = MutableLiveData<Boolean>()
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val loginType = MutableLiveData<Boolean>()

    private val sharedPreferences = application.getSharedPreferences("data", Application.MODE_PRIVATE)

    private fun checkValid(account: Account): Boolean {

        errorUsername.value = if (account.checkUsername()) {
            null
        } else {
            "Tên tài khoản tối thiểu 6 ký tự"
        }

        errorPassword.value = if (account.checkPassword()) {
            null
        } else {
            "Mật khẩu tối thiểu 8 ký tự"
        }

        return errorUsername.value.isNullOrEmpty() && errorPassword.value.isNullOrEmpty()
    }

    fun login() {
        val account = Account(0, username.value.toString(), password.value.toString())

        if (checkValid(account)) {
           loginAccount()
        }
    }

    private fun loginAccount() {
        viewModelScope.launch {
            try {
                val response = LoginService.loginService.login(
                    username.value.toString(),
                    password.value.toString()
                )

                if (response.status) {
                    showError.value = false
                    sharedPreferences.edit().putString("token", response.token).apply()
                    loginType.value = true
                } else {
                    showError.value = true
                    errorMessage.value = response.message
                }
            } catch (e : Exception) {
                showError.value = true
                errorMessage.value = e.message
                Log.d("BBB", e.toString())
            }
        }
    }

}