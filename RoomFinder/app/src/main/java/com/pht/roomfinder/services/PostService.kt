package com.pht.roomfinder.services

import com.pht.roomfinder.utils.App
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.utils.DataLocal
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PostService {

    companion object {
        private val token = DataLocal.getInstance().getString(Const.TOKEN)
        private val headerInterceptor = Interceptor { chain ->
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

    @GET("list-home.php")
    suspend fun listHome(): Response<PostListResponse>
}