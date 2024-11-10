package com.pht.roomfinder.services

import com.pht.roomfinder.model.Post

class SearchResponse(
    val status: Boolean,
    val message: String,
    val data: List<Post>
)