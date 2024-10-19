package com.pht.roomfinder.objects

class Account(
    val id: Int,
    private val username: String,
    private val password: String) {

    fun checkUsername() : Boolean {
        return username.length >= 6
    }

    fun checkPassword() : Boolean {
        return password.length >= 8
    }
}