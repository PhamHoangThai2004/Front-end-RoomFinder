package com.pht.roomfinder.helper

data class UserState(
    var name: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var oldPassword: String? = null,
    var newPassword: String? = null,
    var confirmPassword: String? = null
)