package com.pht.roomfinder.objects

import android.util.Patterns
import java.io.Serializable

class User(
    val userId: Int,
    val roleID: Int,
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String) : Serializable {

    fun checkEmail() : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun checkPassword() : Boolean {
        return password.length >= 8
    }

    fun checkRepeatPassword(repeatPassword : String) : Boolean {
        return password == repeatPassword
    }
}