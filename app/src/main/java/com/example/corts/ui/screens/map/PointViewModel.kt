package com.example.corts.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.corts.data.model.PointUiState
import com.example.corts.data.repository.pointRepositories.PointRepository
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointViewModel @Inject constructor(
    private val pointRepository: PointRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PointUiState>(
        PointUiState(coordinatesList = emptyList())
    )

    val uiState: StateFlow<PointUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            pointRepository.getAllPoints().collect { points ->
                _uiState.value = _uiState.value.copy(coordinatesList = points.map {
                    Point.fromLngLat(it.longitude, it.latitude)
                })
            }
        }
    }

    fun insertPoint(point: Point, date: String) {
        viewModelScope.launch {
            val roundedLongitude = "%.4f".format(point.longitude()).toDouble() // rounds to 4 decimal places
            val roundedLatitude = "%.4f".format(point.latitude()).toDouble() // rounds to 4 decimal places


            pointRepository.insertPoint(
                com.example.corts.data.local.entity.Point(
                    longitude = roundedLongitude,
                    latitude = roundedLatitude,
                    date = date
                )
            )
        }

        viewModelScope.launch {
            pointRepository.getAllPoints().collect { points ->
                _uiState.value = _uiState.value.copy(coordinatesList = points.map {
                    Point.fromLngLat(it.longitude, it.latitude)
                })
            }
        }
    }

    /* fun syncLDBWithRTDB() {
        // takes all the point from the local data base and inserts them to the rtdb
        viewModelScope.launch {
            pointRepository.getAllPoints().collect { points ->
                _uiState.value = _uiState.value.copy(coordinatesList = points.map {
                    Point.fromLngLat(it.longitude, it.latitude)
                })

                pointRepository.insertPointsToFirebase(points)
            }
        }
    } */
    fun syncLDBWithRTDB() {
        viewModelScope.launch {
            pointRepository.getAllPoints().collect { points ->
                _uiState.value = _uiState.value.copy(coordinatesList = points.map {
                    Point.fromLngLat(it.longitude, it.latitude)
                })

                pointRepository.insertPointsToFirebase(points) {
                    this.cancel() // Cancel the coroutine when all points have been inserted
                }
            }
        }
    }





}
