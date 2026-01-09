package com.example.ecotracker.data.remote.api
import com.example.ecotracker.data.remote.model.TipResponse
import retrofit2.http.GET

interface TipsApi {
    @GET("tips/daily")
    suspend fun getDailyTip() : TipResponse
}