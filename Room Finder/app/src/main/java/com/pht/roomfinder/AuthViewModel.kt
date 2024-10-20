package com.pht.roomfinder

import android.app.Application
import android.content.Intent
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.pht.roomfinder.api.AccountService
import com.pht.roomfinder.objects.Account
import com.pht.roomfinder.users.home.HomeActivity
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val errorUsername = MutableLiveData<String>()
    val errorPassword = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val errorRepeat = MutableLiveData<String>()

    val showError = MutableLiveData<Boolean>()

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeatPassword = MutableLiveData<String>()

    val status = MutableLiveData<Boolean>()
    val moveFragment = MutableLiveData<Boolean>()
    val intentEvent = MutableLiveData<Intent>()

    private val sharedPreferences = application.getSharedPreferences("data", Application.MODE_PRIVATE)

    fun moveLogin() {
        moveFragment.value = true
    }

    fun moveRegister() {
        moveFragment.value = false
    }

    fun login() {
        val account = Account(0, username.value.toString(), password.value.toString())

        if (checkValid(account)) {
            loginAccount()
        }
    }

    fun register() {
        val account = Account(0, username.value.toString(), password.value.toString())

        if (checkValid(account)) {
            registerAccount()
        }
    }

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

        if(moveFragment.value == false) {
            errorRepeat.value = if (account.checkRepeatPassword(repeatPassword.value.toString())) {
                null
            } else {
                "Mật khẩu nhắc lại phải trùng với mật khẩu đã nhập"
            }
        }

        return errorUsername.value.isNullOrEmpty() && errorPassword.value.isNullOrEmpty() && errorRepeat.value.isNullOrEmpty()
    }

    private fun registerAccount() {
        viewModelScope.launch {
            try {
                val response = AccountService.accountService.register(
                    username.value.toString(),
                    password.value.toString()
                )

                if(response.status) {
                    showError.value = false
                    sharedPreferences.edit().putString("token", response.token).apply()
                    directLogin()
                } else {
                    showError.value = true
                    errorMessage.value = response.message
                }
            } catch (e: Exception) {
                showError.value = true
                errorMessage.value = e.message
            }
        }
    }

    private fun loginAccount() {
        viewModelScope.launch {
            try {
                val response = AccountService.accountService.login(
                    username.value.toString(),
                    password.value.toString()
                )

                if (response.status) {
                    showError.value = false
                    sharedPreferences.edit().putString("token", response.token).apply()
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
                        if(response.data.role == "User") {
                            val intent = Intent(getApplication(), HomeActivity::class.java)
                            intent.putExtra("accountID", response.data.accountID)
                            intentEvent.postValue(intent)
                        }
                    }
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
    }

}