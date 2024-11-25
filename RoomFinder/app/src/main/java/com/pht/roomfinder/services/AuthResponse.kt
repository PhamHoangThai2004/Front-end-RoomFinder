package com.pht.roomfinder.services

import com.pht.roomfinder.model.User

class AuthResponse(
    val status: Boolean,
    val message: String,
    val token: String,
    val data: TokenData,
//    val dataUser: User
)