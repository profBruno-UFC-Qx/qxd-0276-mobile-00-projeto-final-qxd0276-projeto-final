package com.pegai.app.model

data class ProductDetails(
    val id: String,
    val title: String,
    val price: String,
    val description: String,
    val category: String,
    val rating: Double,
    val totalReviews: Int,
    val images: List<String>,
    val owner: ProductOwner,
    val reviews: List<Review>
)

data class ProductOwner(
    val id: String,
    val name: String,
    val rating: String,
    val photoUrl: String
)

data class Review(
    val authorName: String,
    val comment: String,
    val rating: Int,
    val date: String
)