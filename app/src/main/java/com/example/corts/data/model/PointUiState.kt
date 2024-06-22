package com.example.corts.data.model


import com.example.corts.data.local.entity.Point

data class PointUiState constructor(
    val coordinatesList: List<Point> = emptyList()
)