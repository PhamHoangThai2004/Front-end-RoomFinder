package com.pht.roomfinder.model

import java.sql.Timestamp

class Post(
    val postID: Int?,
    val user: User?,
    val category: Category?,
    val location: Location?,
    val title: String?,
    val description: String?,
    val price: Double?,
    val acreage: Double?,
    val address: String?,
    val tym: Int?,
    val bonus: String?,
    val createdAt: Timestamp?,
    val expireAt: Timestamp?,
    val images: List<Images>?
)