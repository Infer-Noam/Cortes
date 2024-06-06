package com.example.Cortés.ui.panes.Map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.corts.data.local.entity.Point
import com.example.corts.ui.panes.map.PointViewModel
import com.mapbox.geojson.Feature
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.layers.generated.BackgroundColor
import com.mapbox.maps.extension.compose.style.layers.generated.BackgroundLayer
import com.mapbox.maps.extension.compose.style.layers.generated.BackgroundOpacity
import com.mapbox.maps.extension.compose.style.layers.generated.CircleColor
import com.mapbox.maps.extension.compose.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.compose.style.layers.generated.CircleRadius
import com.mapbox.maps.extension.compose.style.sources.generated.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.GeoJsonSourceState
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings


@OptIn(MapboxExperimental::class)
@Composable
fun Map(modifier: Modifier = Modifier, currentCoordinates: Point, navController: NavController) {
    val pointViewModel: PointViewModel = hiltViewModel()
    val pointUiState by pointViewModel.uiState.collectAsStateWithLifecycle()

    // Toast.makeText(context, currentCoordinates.longitude().toString(), Toast.LENGTH_SHORT).show()

    val geoJsonSource: GeoJsonSourceState = rememberGeoJsonSourceState {
        // Initialize the GeoJSONData with the list of points


        val features = (pointUiState.coordinatesList + currentCoordinates).map {
            Feature.fromGeometry(
                com.mapbox.geojson.Point.fromLngLat(
                    it.longitude,
                    it.latitude
                )
            )
        }



        data = GeoJSONData(
            features
            //LineString.fromLngLats(allPoints)
        )
    }

    LaunchedEffect(pointUiState.coordinatesList, currentCoordinates) {
        val updatedFeatures = (pointUiState.coordinatesList + currentCoordinates).map {
            Feature.fromGeometry(com.mapbox.geojson.Point.fromLngLat(it.longitude, it.latitude))
        }
        geoJsonSource.data = GeoJSONData(updatedFeatures)
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(com.mapbox.geojson.Point.fromLngLat(currentCoordinates.longitude, currentCoordinates.latitude))
            zoom(10.0)
            pitch(0.0)
        }
    }

    val mapBoxUiSettings: GesturesSettings by remember {
        mutableStateOf(GesturesSettings {
            rotateEnabled = true
            pinchToZoomEnabled = true
            pitchEnabled = true
        })
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        gesturesSettings = mapBoxUiSettings,
        style = {
            // Load Mapbox standard style
            MapboxStandardStyle(
                middleSlot = {
                    BackgroundLayer(
                        backgroundColor = BackgroundColor(Color.DarkGray),
                        backgroundOpacity = BackgroundOpacity(0.85)
                    )
                },
                topSlot = {   // Insert a larger gray circle layer with the given geoJsonSource.
                    CircleLayer(
                        sourceState = geoJsonSource,
                        circleColor = CircleColor(Color.DarkGray),
                        circleRadius = CircleRadius(15.0),
                        // filter = Filter.eq("id", "grayCircle") // Unique identifier
                    )
                    // Insert a smaller white circle layer on top of the gray circle.
                    CircleLayer(
                        sourceState = geoJsonSource,
                        circleColor = CircleColor(Color.White),
                        circleRadius = CircleRadius(10.0),
                        // filter = Filter.eq("id", "whiteCircle") // Unique identifier
                    )}
            )
        }
    ) {
        // You can draw additional map markers or customize further here.
    }

    /* LaunchedEffect(Unit) {
        delay(200)
        mapViewportState.flyTo(
            cameraOptions = cameraOptions {
                center(currentCoordinates)
                zoom(12.0)
            },
            animationOptions = MapAnimationOptions.mapAnimationOptions { duration(5000) },
        )
    } */
}
