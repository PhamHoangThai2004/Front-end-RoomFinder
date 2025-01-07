package com.pht.roomfinder.response

import com.pht.roomfinder.model.Post

class PostResponse(
    val status: Boolean,
    val message: String,
    val data: Post
)