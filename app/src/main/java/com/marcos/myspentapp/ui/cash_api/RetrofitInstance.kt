package com.marcos.myspentapp.ui.cash_api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: CambioService by lazy {
        Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/771a2da841336b5a31a9c74d/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CambioService::class.java)
    }
}
