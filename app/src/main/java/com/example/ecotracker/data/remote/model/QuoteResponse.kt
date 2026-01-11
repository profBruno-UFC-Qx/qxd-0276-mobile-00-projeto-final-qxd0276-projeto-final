package com.example.ecotracker.data.remote.model

import com.google.gson.annotations.SerializedName

data class QuoteResponse(
    @SerializedName("q") val text: String,
    @SerializedName("a") val author: String
)