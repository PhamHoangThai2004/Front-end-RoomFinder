package com.pht.roomfinder.api

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val token: String
)