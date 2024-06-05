package com.example.Cortés.ui.Map

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
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
fun Map(modifier: Modifier = Modifier, coordinatesList: List<Point>, currentCoordinates: Point, context: Context) {


    Toast.makeText(context, currentCoordinates.longitude().toString(), Toast.LENGTH_SHORT).show()

 val geoJsonSource: GeoJsonSourceState = rememberGeoJsonSourceState {
        // Initialize the GeoJSONData with the list of points


        val features = (coordinatesList + currentCoordinates).map {
            Feature.fromGeometry(
                Point.fromLngLat(
                    it.longitude(),
                    it.latitude()
                )
            )
        }



        data = GeoJSONData(
            features
            //LineString.fromLngLats(allPoints)
        )
    }

    LaunchedEffect(coordinatesList, currentCoordinates) {
        val updatedFeatures = (coordinatesList + currentCoordinates).map {
            Feature.fromGeometry(Point.fromLngLat(it.longitude(), it.latitude()))
        }
        geoJsonSource.data = GeoJSONData(updatedFeatures)
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(currentCoordinates)
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
