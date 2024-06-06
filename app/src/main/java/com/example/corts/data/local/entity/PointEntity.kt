package com.example.corts.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Point(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,

    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val date: String = ""
)