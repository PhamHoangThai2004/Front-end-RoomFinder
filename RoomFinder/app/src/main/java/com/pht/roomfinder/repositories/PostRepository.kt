package com.pht.roomfinder.repositories

import android.util.Log
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.response.PostListResponse
import com.pht.roomfinder.response.PostResponse
import com.pht.roomfinder.response.SearchResponse
import com.pht.roomfinder.response.UserResponse
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
                        "List filter failed: ${response.errorBody()?.string()}"
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun postDetail(postID: Int): Result<PostResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<PostResponse> = postService.postDetail(postID)
                if (response.isSuccessful) Result.success(response.body()!!)
                else {
                    Result.failure(
                        Exception(
                            "Post detail failed: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun userDetail(userId: Int): Result<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<UserResponse> = postService.userDetail(userId)
                if (response.isSuccessful) Result.success(response.body()!!)
                else {
                    Result.failure(
                        Exception(
                            "Detail user failed: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun likePost(postId: Int, isLiked: Boolean): Result<PostResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<PostResponse> = postService.likePost(postId, isLiked)
                if (response.isSuccessful) Result.success(response.body()!!)
                else {
                    Result.failure(
                        Exception(
                            "Like post failed: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun favoritePost(): Result<SearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<SearchResponse> = postService.favoritePost()
                if (response.isSuccessful) Result.success(response.body()!!)
                else {
                    Result.failure(
                        Exception(
                            "Favorite post failed: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun listPost(isExpired: Boolean): Result<SearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<SearchResponse> = postService.listPost(isExpired)
                if (response.isSuccessful) Result.success(response.body()!!)
                else {
                    Result.failure(
                        Exception(
                            "List posts failed: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun newPost(post: Post): Result<PostResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<PostResponse> = postService.newPost(post)
                if (response.isSuccessful) Result.success(response.body()!!)
                else {
                    Result.failure(
                        Exception(
                            "New post failed: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deletePost(postId: Int): Result<PostResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<PostResponse> = postService.deletePost(postId)
                if (response.isSuccessful) Result.success(response.body()!!)
                else {
                    Result.failure(
                        Exception(
                            "Delete post failed: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updatePost(post: Post): Result<PostResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<PostResponse> = postService.updatePost(post)
                if (response.isSuccessful) Result.success(response.body()!!)
                else {
                    Result.failure(
                        Exception(
                            "Update post failed: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}