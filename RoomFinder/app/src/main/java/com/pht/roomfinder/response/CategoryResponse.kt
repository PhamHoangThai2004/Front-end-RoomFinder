package com.pht.roomfinder.response

import com.pht.roomfinder.model.Category

class CategoryResponse(
    val status: Boolean,
    val message: String,
    val data: List<Category>
)