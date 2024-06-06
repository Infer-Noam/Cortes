package com.example.corts.data.model

import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental

data class PointUiState @OptIn(MapboxExperimental::class) constructor(
    val coordinatesList: List<Point> = emptyList()
)