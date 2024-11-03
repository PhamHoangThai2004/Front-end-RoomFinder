package com.pht.roomfinder.services

import com.pht.roomfinder.model.User

class TokenData(
    val iat: Int,
    val exp: String,
    val user: User
)