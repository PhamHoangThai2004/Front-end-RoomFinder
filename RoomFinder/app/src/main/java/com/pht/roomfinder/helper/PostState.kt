package com.pht.roomfinder.helper

data class PostState(
    var categoryName: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    val locationIsNull: Boolean = true,
    var address: String? = null,
    var title: String? = null,
    var description: String? = null,
    var price: String? = null,
    var acreage: String? = null,
    val area: String? = null,
    var bonus: String? = null,
    val expireAt: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isBack: Boolean = false,
    val isExpired: Boolean = false
)