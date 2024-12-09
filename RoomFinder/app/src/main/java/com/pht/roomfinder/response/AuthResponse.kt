package com.pht.roomfinder.response

class AuthResponse(
    val status: Boolean,
    val message: String,
    val token: String,
    val data: TokenData,
//    val dataUser: User
)