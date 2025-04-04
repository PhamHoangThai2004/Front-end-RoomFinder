package com.pht.roomfinder.services

import com.pht.roomfinder.helper.DataLocal
import com.pht.roomfinder.model.Post
import com.pht.roomfinder.response.PostListResponse
import com.pht.roomfinder.response.PostResponse
import com.pht.roomfinder.response.SearchResponse
import com.pht.roomfinder.response.UserResponse
import com.pht.roomfinder.utils.Const
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {

    companion object {

        private val headerInterceptor = Interceptor { chain ->
            val token = DataLocal.getInstance().getString(Const.TOKEN)
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Const.HTTP_API + "post/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val postService: PostService = retrofit.create(PostService::class.java)
    }

    @GET("list-group.php")
    suspend fun listGroup(): Response<PostListResponse>

    @GET("list-search.php")
    suspend fun listSearch(@Query("keySearch") keySearch: String): Response<SearchResponse>

    @GET("post-filter.php")
    suspend fun postFilter(
        @Query("categoryName") categoryName: String,
        @Query("area") area: String,
        @Query("minPrice") minPrice: Int,
        @Query("maxPrice") maxPrice: Int,
        @Query("minAcreage") minAcreage: Int,
        @Query("maxAcreage") maxAcreage: Int
    ): Response<SearchResponse>

    @GET("post.php")
    suspend fun postDetail(@Query("postId") postID: Int): Response<PostResponse>

    @FormUrlEncoded
    @POST("user-detail.php")
    suspend fun userDetail(@Field("userId") userId: Int): Response<UserResponse>

    @GET("like-post.php")
    suspend fun likePost(
        @Query("postId") postId: Int,
        @Query("isLiked") isLiked: Boolean
    ): Response<PostResponse>

    @GET("favorite-post.php")
    suspend fun favoritePost(): Response<SearchResponse>

    @GET("list-post.php")
    suspend fun listPost(@Query("isExpired") isExpired: Boolean): Response<SearchResponse>

    @POST("post.php")
    suspend fun newPost(@Body post: Post): Response<PostResponse>

    @DELETE("post.php/{postId}")
    suspend fun deletePost(@Path("postId") postId: Int): Response<PostResponse>

    @PUT("post.php")
    suspend fun updatePost(@Body post: Post): Response<PostResponse>

    @GET("lists.php")
    suspend fun listRandom(
        @Query("typeSearch") typeSearch: Int,
        @Query("area") area: String
    ): Response<SearchResponse>

}