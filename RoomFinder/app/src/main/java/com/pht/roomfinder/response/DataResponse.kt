package com.pht.roomfinder.response

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

class AreaResponse(
    val exitcode: Int,
    val data: AreaData,
    val message: String
)

class AreaData(
    val nItems: Int,
    val nPages: Int,
    val data: List<Area>
)

class Area(val name: String, val code: String) {
    override fun toString(): String {
        return name
    }
}