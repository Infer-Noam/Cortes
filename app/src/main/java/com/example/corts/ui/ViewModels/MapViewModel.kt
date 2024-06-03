package com.example.corts.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.corts.data.MapRepository
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {
    // Other ViewModel logic

    // Initialize uiState with default values
    private val _uiState = MutableStateFlow<MapUiState>(
        MapUiState(coordinatesList = emptyList())
    )

    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            mapRepository.getAllPoints().collect { points ->
                _uiState.value = _uiState.value.copy(coordinatesList = points.map {
                    Point.fromLngLat(it.longitude, it.latitude)
                })
            }
        }
    }

    fun insertPoint(point: Point) {
        viewModelScope.launch {
            val roundedLongitude = "%.4f".format(point.longitude()).toDouble() // rounds to 4 decimal places
            val roundedLatitude = "%.4f".format(point.latitude()).toDouble() // rounds to 4 decimal places


            mapRepository.insertPoint(com.example.corts.data.local.database.Point(longitude = roundedLongitude, latitude =  roundedLatitude))
        }

        viewModelScope.launch {
            mapRepository.getAllPoints().collect { points ->
                _uiState.value = _uiState.value.copy(coordinatesList = points.map {
                    Point.fromLngLat(it.longitude, it.latitude)
                })
            }
        }
    }


    data class MapUiState(
        val coordinatesList: List<Point> = emptyList()
    )
}