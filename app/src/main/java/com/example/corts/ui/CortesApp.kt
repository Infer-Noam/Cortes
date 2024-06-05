package com.example.corts.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.Cortés.ui.Map.Map
import com.example.corts.Cortés
import com.example.corts.ui.navigationBars.BottomAppBar
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.style.sources.generated.Coordinates

@Composable
fun CortésApp(coordinatesList: List<Point>, longitude: Double, latitude: Double){
    Scaffold ( bottomBar = { BottomAppBar() }){ paddingValues ->
        Map(modifier = Modifier.padding(paddingValues),
            coordinatesList = coordinatesList,
            currentCoordinates = Point.fromLngLat(longitude, latitude),
            context = LocalContext.current
        )
    }
}

@Composable
@Preview
fun CortésAppPreview(){
    CortésApp(coordinatesList = emptyList(), longitude = 0.1, latitude = 0.1)
}