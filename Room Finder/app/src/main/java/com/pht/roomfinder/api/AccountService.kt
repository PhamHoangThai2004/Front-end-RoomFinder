package com.pht.roomfinder.api

import com.pht.roomfinder.objects.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface AccountService {

    companion object {

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Const.HTTP_API + "authentication/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val accountService: AccountService = retrofit.create(AccountService::class.java)
    }

    @POST("login.php")
    suspend fun login(@Body user: User): AuthResponse

    @FormUrlEncoded
    @POST("checktoken.php")
    suspend fun checkToken(@Field ("token") token: String): AuthResponse

    @POST("register.php")
    suspend fun register(@Body user: User): AuthResponse

}