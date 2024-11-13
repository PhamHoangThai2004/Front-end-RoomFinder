package com.pht.roomfinder.services

import com.pht.roomfinder.model.User
import com.pht.roomfinder.utils.Const
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface UserService {

    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Const.HTTP_API + "authentication/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userService: UserService = retrofit.create(UserService::class.java)
    }

    @POST("login.php")
    suspend fun login(@Body user: User): Response<AuthResponse>

    @GET("check-token.php")
    suspend fun checkToken(@Header("Authorization") token: String): Response<AuthResponse>

    @POST("register.php")
    suspend fun register(@Body user: User): Response<AuthResponse>

    @FormUrlEncoded
    @POST("check-otp.php")
    suspend fun checkOTP(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("forgot-password.php")
    suspend fun forgotPassword(@Field("email") email: String): Response<AuthResponse>

    @FormUrlEncoded
    @POST("confirm-email.php")
    suspend fun confirmEmail(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("create-password.php")
    suspend fun createPassword(
        @Field("email") email: String,
        @Field("newPassword") newPassword: String
    ): Response<AuthResponse>

}