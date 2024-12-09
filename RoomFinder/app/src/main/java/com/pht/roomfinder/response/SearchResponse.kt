package com.pht.roomfinder.response

import com.pht.roomfinder.model.Post

class SearchResponse(
    val status: Boolean,
    val message: String,
    val data: List<Post>
)