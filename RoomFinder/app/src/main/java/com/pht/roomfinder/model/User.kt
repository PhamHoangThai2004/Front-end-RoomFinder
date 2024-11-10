package com.pht.roomfinder.model

import android.util.Patterns
import java.io.Serializable
import java.sql.Timestamp

class User(
    val userId: Int?,
    val role: Role?,
    var email: String?,
    var password: String?,
    var name: String?,
    var phoneNumber: String?,
    val createAt: Timestamp?
) : Serializable {

    fun checkEmail(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email ?: "").matches()
    }

    fun checkPassword(): Boolean {
        return (password ?: "").length >= 8
    }

    fun checkPhoneNumber(): Boolean {
        return Patterns.PHONE.matcher(phoneNumber ?: "").matches()
    }

    fun checkName(): Boolean {
        return (name ?: "").length >= 2
    }
}