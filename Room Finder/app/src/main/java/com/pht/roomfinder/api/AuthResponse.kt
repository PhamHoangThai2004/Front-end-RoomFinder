package com.pht.roomfinder.api

class AuthResponse(
    val status: Boolean,
    val message: String,
    val token: String,
    val data: TokenData
)