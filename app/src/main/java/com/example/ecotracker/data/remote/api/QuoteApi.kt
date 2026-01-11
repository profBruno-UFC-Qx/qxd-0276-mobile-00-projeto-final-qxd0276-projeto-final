package com.example.ecotracker.data.remote.api
import com.example.ecotracker.data.remote.model.QuoteResponse
import retrofit2.http.GET

interface QuoteApi {
    @GET("today")
    suspend fun getDailyQuote() : List<QuoteResponse>
}