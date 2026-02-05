package com.example.ecotracker.data.local.entity

import com.google.android.gms.maps.model.LatLng

data class HabitMapItem(
    val id: Long,
    val name: String,
    val latLng: LatLng
)