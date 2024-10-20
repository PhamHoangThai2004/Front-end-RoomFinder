package com.pht.roomfinder.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface AccountService {

    companion object {

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Const.HTTP_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val accountService: AccountService = retrofit.create(AccountService::class.java)
    }

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(@Field ("username") username: String, @Field ("password") password: String): AccountResponse

    @FormUrlEncoded
    @POST("check_token.php")
    suspend fun checkToken(@Field ("token") token: String): TokenResponse

    @FormUrlEncoded
    @POST("register.php")
    suspend fun register(@Field ("username") username: String, @Field ("password") password: String): AccountResponse

}