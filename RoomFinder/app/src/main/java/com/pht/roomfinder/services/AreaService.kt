package com.pht.roomfinder.services

import com.pht.roomfinder.response.AreaResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AreaService {
//    https://vn-public-apis.fpo.vn/provinces/getAll?limit=-1
//    https://vn-public-apis.fpo.vn/districts/getByProvince?provinceCode=33&limit=-1
//    https://vn-public-apis.fpo.vn/wards/getByDistrict?districtCode=329&limit=-1

    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://vn-public-apis.fpo.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val areaService: AreaService = retrofit.create(AreaService::class.java)
    }

    @GET("provinces/getAll")
    suspend fun getProvinces(@Query("limit") limit: Int): AreaResponse

    @GET("districts/getByProvince")
    suspend fun getDistricts(
        @Query("provinceCode") provinceCode: String,
        @Query("limit") limit: Int
    ): AreaResponse

    @GET("wards/getByDistrict")
    suspend fun getWards(
        @Query("districtCode") districtCode: String,
        @Query("limit") limit: Int
    ): AreaResponse


}