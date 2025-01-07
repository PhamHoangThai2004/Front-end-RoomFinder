package com.pht.roomfinder.response

import com.google.gson.annotations.SerializedName
import com.pht.roomfinder.model.Category
import com.pht.roomfinder.model.Images
import com.pht.roomfinder.model.Location
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.model.User

class UserData(
    val user: User,
    val totalPosts: Int,
    val listPost: List<Post>,
)

class TokenData(
    val iat: Int,
    val exp: String,
    val user: User
)

class ListGroupData(
    val titleList: String,
    val listPost: List<Post>
)

//class PostData(
//    val postID: Int?,
//    val user: User?,
//    val category: Category?,
//     val location: Location?,
//     val title: String?,
//     val description: String?,
//     val price: Double?,
//     val acreage: Double?,
//     val area: String?,
//     val bonus: String?,
//     var createdAt: String?,
//     val expireAt: String?,
//     val images: List<Images>?,
//    val tym: Int?,
//    val isLiked: Boolean?)

//class PostData(
//    @SerializedName("postID") override val postID: Int?,
//    @SerializedName("user") override val user: User?,
//    @SerializedName("category") override val category: Category?,
//    @SerializedName("location") override val location: Location?,
//    @SerializedName("title") override val title: String?,
//    @SerializedName("description") override val description: String?,
//    @SerializedName("price") override val price: Double?,
//    @SerializedName("acreage") override val acreage: Double?,
//    @SerializedName("area") override val area: String?,
//    @SerializedName("bonus") override val bonus: String?,
//    @SerializedName("createdAt") override var createdAt: String?,
//    @SerializedName("expireAt") override val expireAt: String?,
//    @SerializedName("images") override val images: List<Images>?,
//    @SerializedName("tym") val tym: Int?,
//    @SerializedName("isLiked") val isLiked: Boolean?
//) : Post(postID, user, category, location, title, description, price, acreage, area, bonus, createdAt, expireAt, images)
