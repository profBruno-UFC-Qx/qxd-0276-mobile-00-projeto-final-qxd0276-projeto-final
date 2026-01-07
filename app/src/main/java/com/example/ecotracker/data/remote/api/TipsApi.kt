package com.example.ecotracker.data.remote.api
import retrofit2.http.GET

interface TipsApi {
    @GET("tips/daily")
    suspend fun getDailyTip() : TipResponse
}