package com.example.corts.ui.panes.map

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.example.corts.data.local.entity.Point
import com.example.corts.data.model.PointUiState
import com.example.corts.data.repository.pointRepositories.PointRepository
import com.example.corts.ui.animations.loading.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PointViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pointRepository: PointRepository
) : LoadingViewModel() {

    private val _uiState = MutableStateFlow<PointUiState>(
        PointUiState(coordinatesList = emptyList())
    )

    val uiState: StateFlow<PointUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            pointRepository.getAllPoints().collect { points ->
                _uiState.value = _uiState.value.copy(coordinatesList = points)
            }
        }
    }

    fun insertPoint(point: com.mapbox.geojson.Point, date: String) {
        viewModelScope.launch {
            val roundedLongitude =
                "%.4f".format(point.longitude()).toDouble() // rounds to 4 decimal places
            val roundedLatitude =
                "%.4f".format(point.latitude()).toDouble() // rounds to 4 decimal places


            pointRepository.insertPoint(
                Point(
                    longitude = roundedLongitude,
                    latitude = roundedLatitude,
                    date = date
                )
            )
        }

        viewModelScope.launch {
            pointRepository.getAllPoints().collect { points ->
                _uiState.value = _uiState.value.copy(coordinatesList = points)
            }
        }
    }

    fun syncLDBWithRTDB() {
        viewModelScope.launch {
            //startLoading()
            pointRepository.getAllPoints().collect { points ->
                _uiState.value = _uiState.value.copy(coordinatesList = points)

                pointRepository.insertPointsToFirebase(points) {
                    this.cancel() // Cancel the coroutine when all points have been inserted
                  //  stopLoading()
                    Toast.makeText(context, "Sync was successful", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun deleteData() {
        viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            val success = async { pointRepository.deleteAllPoints() }.await()

            viewModelScope.launch(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(context, "Deletion was successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                stopLoading()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun exportDataToFile() {
        startLoading()
        val groupedPoints = _uiState.value.coordinatesList.groupBy { it.date }

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val json = groupedPoints.entries.joinToString("\n\n") { (date, points) ->
            val formattedDate = date.format(formatter)
            val pointsString = points.joinToString(", ") { point ->
                "(${point.longitude}, ${point.latitude})"
            }
            val lineOfDashes = "-".repeat(123) // Adjust the length as needed
            "$formattedDate\n$pointsString\n$lineOfDashes"
        }

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "coordinates_data.txt")
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues)
            val outputStream = uri?.let { resolver.openOutputStream(it) }

            outputStream?.use { it.write(json.toByteArray()) }

            Toast.makeText(context, "Export was successful", Toast.LENGTH_SHORT).show()

            // Automatically open the exported file
            uri?.let { fileUri ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = fileUri
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        } catch (e: IOException) {
            // Show an error message to the user
            // (You can replace this with your actual UI feedback)
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        stopLoading()
    }
}