package com.pht.roomfinder.repositories

import com.pht.roomfinder.services.PostListResponse
import com.pht.roomfinder.services.PostService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class PostRepository (private val postService: PostService) {

    suspend fun getListHome(): Result<PostListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<PostListResponse> = postService.listHome()

                if (response.isSuccessful) Result.success(response.body()!!)
                else Result.failure(Exception("List home failed: ${response.errorBody()?.string()}"))
            }
            catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}