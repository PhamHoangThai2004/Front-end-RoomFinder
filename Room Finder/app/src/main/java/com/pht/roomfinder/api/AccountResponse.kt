package com.pht.roomfinder.api

data class AccountResponse(
    val status: Boolean,
    val message: String,
    val token: String
)