package com.pht.roomfinder.services

import com.pht.roomfinder.helper.DataLocal
import com.pht.roomfinder.response.CategoryResponse
import com.pht.roomfinder.utils.Const
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CategoryService {
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
            .baseUrl(Const.HTTP_API + "category/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val categoryService: CategoryService = retrofit.create(CategoryService::class.java)
    }

    @GET("list-category.php")
    suspend fun listCategory(): Response<CategoryResponse>
}