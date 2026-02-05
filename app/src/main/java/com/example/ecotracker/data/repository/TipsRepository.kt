package com.example.ecotracker.data.repository

import com.example.ecotracker.data.remote.RetrofitInstance
import com.example.ecotracker.data.remote.model.QuoteResponse

class QuoteRepository {
    suspend fun getDailyQuote(): QuoteResponse {
        return RetrofitInstance.api
            .getDailyQuote()
            .first()
    }
}



