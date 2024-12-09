package com.pht.roomfinder.repositories

import com.pht.roomfinder.response.PostListResponse
import com.pht.roomfinder.response.SearchResponse
import com.pht.roomfinder.services.PostService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class PostRepository(private val postService: PostService) {

    suspend fun getListGroup(): Result<PostListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<PostListResponse> = postService.listGroup()

                if (response.isSuccessful) Result.success(response.body()!!)
                else Result.failure(
                    Exception(
                        "List group failed: ${
                            response.errorBody()?.string()
                        }"
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getListSearch(keySearch: String): Result<SearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<SearchResponse> = postService.listSearch(keySearch)
                if (response.isSuccessful) Result.success(response.body()!!)
                else Result.failure(
                    Exception(
                        "List search failed: ${
                            response.errorBody()?.string()
                        }"
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun postFilter(
        categoryName: String,
        area: String,
        minPrice: Int,
        maxPrice: Int,
        minAcreage: Int,
        maxAcreage: Int
    ): Result<SearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<SearchResponse> = postService.postFilter(
                    categoryName,
                    area,
                    minPrice,
                    maxPrice,
                    minAcreage,
                    maxAcreage
                )
                if (response.isSuccessful) Result.success(response.body()!!)
                else Result.failure(
                    Exception(
                        "List search failed: ${response.errorBody()?.string()}"
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}