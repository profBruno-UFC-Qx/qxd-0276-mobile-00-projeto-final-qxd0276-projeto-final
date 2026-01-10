package com.example.bookkeeper.data.api

import com.google.gson.annotations.SerializedName

data class GoogleBooksResponse(
    @SerializedName("items") val items: List<BookItem>?
)

data class BookItem(
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo?
)

data class VolumeInfo(
    @SerializedName("title") val title: String?,
    @SerializedName("authors") val authors: List<String>?,
    @SerializedName("description") val description: String?,
    @SerializedName("pageCount") val pageCount: Int?,
    @SerializedName("imageLinks") val imageLinks: ImageLinks?
)

data class ImageLinks(
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("smallThumbnail") val smallThumbnail: String?
)