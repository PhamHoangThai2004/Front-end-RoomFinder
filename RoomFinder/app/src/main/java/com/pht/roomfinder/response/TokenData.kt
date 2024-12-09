package com.pht.roomfinder.response

import com.pht.roomfinder.model.User

class TokenData(
    val iat: Int,
    val exp: String,
    val user: User
)