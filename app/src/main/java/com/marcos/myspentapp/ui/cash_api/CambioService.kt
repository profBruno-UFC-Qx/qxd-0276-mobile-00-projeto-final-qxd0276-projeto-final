package com.marcos.myspentapp.ui.cash_api

import retrofit2.http.GET
import retrofit2.http.Path

interface CambioService {

    @GET("latest/{base}")
    suspend fun getRates(
        @Path("base") base: String
    ): CambioResponse
}

data class CambioResponse(
    val base_code: String,
    val conversion_rates: Map<String, Double>
)
