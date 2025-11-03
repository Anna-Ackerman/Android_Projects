package com.example.spacexlaunches.data.api

import com.example.spacexlaunches.data.model.Launch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SpaceXApi {

    @GET("launches/past")
    suspend fun getPastLaunches(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null
    ): Response<List<Launch>>

    companion object {
        const val BASE_URL = "https://api.spacexdata.com/v3/"
    }
}