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
    var avatar: String?,
    var phoneNumber: String?,
    var address: String?,
    val createdAt: String?
) : Serializable {

    val totalPosts: Int = 0

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

    fun checkAddress(): Boolean {
        return (address ?: "").length >= 5
    }


    override fun toString(): String {
        return "User(userId=$userId, role=$role, email=$email, password=$password, name=$name, avatar=$avatar, phoneNumber=$phoneNumber, address=$address, createdAt=$createdAt)"

    }
}