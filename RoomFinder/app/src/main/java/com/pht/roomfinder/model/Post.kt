package com.pht.roomfinder.model

import java.io.Serializable

data class Post(
    val postID: Int?,
    val user: User?,
    val category: Category?,
    val location: Location?,
    val title: String?,
    val description: String?,
    val price: Double?,
    val acreage: Double?,
    val area: String?,
    val bonus: String?,
    var createdAt: String?, //Reformat lại thành Timestamp
    val expireAt: String?,
    val images: List<Images>?,
    var tym: Int?,
    var isLiked: Boolean?
) : Serializable {
    override fun toString(): String {
        return "postId: $postID"
    }
}