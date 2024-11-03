package com.pht.roomfinder.services

class   AuthResponse(
    val status: Boolean,
    val message: String,
    val token: String,
    val data: TokenData
)