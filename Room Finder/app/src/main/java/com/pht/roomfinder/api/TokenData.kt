package com.pht.roomfinder.api

class TokenData(
    val iat: Int,
    val exp: String,
    val userID: Int,
    val email: String,
    val roleName: String,
    val name: String,
    val phoneNumber: String
)