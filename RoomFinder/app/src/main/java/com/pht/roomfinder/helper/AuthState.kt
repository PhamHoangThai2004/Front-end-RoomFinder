package com.pht.roomfinder.helper

data class AuthState(
    var email: String? = null,
    var errEmail: String? = null,
    var password: String? = null,
    var errPassword: String? = null,
    var name: String? = null,
    var errName: String? = null,
    var phone: String? = null,
    var errPhone: String? = null,
    var message: String? = null,
    val isOpenOTP: Boolean = false,
    var errOTP: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)