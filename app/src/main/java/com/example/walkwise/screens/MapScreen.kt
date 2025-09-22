package com.example.walkwise.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walkwise.data.SharedViewModel
import com.example.walkwise.graph.Place
import com.example.walkwise.graph.RouteGraph
import com.example.walkwise.modeltraining.ModelTrainer
import com.example.walkwise.network.MapsService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    context: Context
) {
    val graph = RouteGraph.graph

    val startLocation = remember { mutableStateOf<Place?>(null) }
    val destLocation = remember { mutableStateOf<Place?>(null) }

    val startPlaceName = sharedViewModel.myLocation
    val destPlaceName = sharedViewModel.destination

    val startCoordinates = sharedViewModel.startCoordinates
    val destCoordinates = sharedViewModel.destCoordinates

    val walkingTime = remember { mutableStateOf(0.0) }
    val busTravelTime = remember { mutableStateOf(0.0) }
    val busDistance = remember { mutableStateOf(0) }

    LaunchedEffect(startPlaceName, destPlaceName, startCoordinates, destCoordinates) {
        if (startPlaceName.isNotEmpty() && destPlaceName.isNotEmpty()) {
            val startPlace = graph.adjacencyMap.keys.find { it.name.equals(startPlaceName, ignoreCase = true) }
            val destPlace = graph.adjacencyMap.keys.find { it.name.equals(destPlaceName, ignoreCase = true) }

            if (startPlace != null && destPlace != null) {
                startLocation.value = startPlace
                destLocation.value = destPlace

                val mapsService = MapsService(context)
                mapsService.getWalkingAndBusDetails(
                    "${startLocation.value!!.latitude},${startLocation.value!!.longitude}",
                    "${destLocation.value!!.latitude},${destLocation.value!!.longitude}"
                ) { walkingTravelTime, walkingDistance, busTime, busDistanceValue ->
                    Log.d("mytag", "Walking Distance: $walkingDistance meters")
                    Log.d("mytag", "Bus Distance: $busDistanceValue meters")

                    val time = ModelTrainer.getInstance(context).infer(
                        startLocation.value!!.latitude, startLocation.value!!.longitude,
                        destLocation.value!!.latitude, destLocation.value!!.longitude,
                        walkingDistance.toDouble(), busDistanceValue.toDouble()
                    )

                    walkingTime.value = (time.walkingTime / 60).toDouble()
                    busTravelTime.value = (time.busTravelTime / 60).toDouble()
                    busDistance.value = busDistanceValue
                }
            }
        } else if (startCoordinates != null && destCoordinates != null) {
            val startLatLng = startCoordinates.split(",").map { it.toDouble() }
            val destLatLng = destCoordinates.split(",").map { it.toDouble() }

            val mapsService = MapsService(context)
            mapsService.getWalkingAndBusDetails(
                "${startLatLng[0]},${startLatLng[1]}",
                "${destLatLng[0]},${destLatLng[1]}"
            ) { walkingTravelTime, walkingDistance, busTime, busDistanceValue ->
                Log.d("mytag", "Walking Distance: $walkingDistance meters")
                Log.d("mytag", "Bus Distance: $busDistanceValue meters")

                val time = ModelTrainer.getInstance(context).infer(
                    startLatLng[0], startLatLng[1],
                    destLatLng[0], destLatLng[1],
                    walkingDistance.toDouble(), busDistanceValue.toDouble()
                )

                walkingTime.value = (time.walkingTime / 60).toDouble()
                busTravelTime.value = (time.busTravelTime / 60).toDouble()
                busDistance.value = busDistanceValue
            }
        }
    }

    val firstPoint = LatLng(startLocation.value?.latitude ?: startCoordinates?.split(",")?.get(0)?.toDouble() ?: 0.0, startLocation.value?.longitude ?: startCoordinates?.split(",")?.get(1)?.toDouble() ?: 0.0)
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstPoint, 18f)
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            modifier = Modifier.matchParentSize(),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false // Disable default zoom controls
            )
        ) {
            if (startLocation.value != null && destLocation.value != null) {
                val shortestPath = graph.dijkstra(startLocation.value!!, destLocation.value!!)
                val polylinePoints = placesToLatLng(shortestPath)

                Marker(
                    state = MarkerState(position = polylinePoints.first()),
                    title = "Start",
                    snippet = "Starting point"
                )
                Marker(
                    state = MarkerState(position = polylinePoints.last()),
                    title = "Destination",
                    snippet = "Destination point"
                )

                Polyline(
                    points = polylinePoints,
                    color = Color.Blue,
                    pattern = listOf(Dot(), Gap(10f))
                )

                LaunchedEffect(polylinePoints) {
                    val boundsBuilder = LatLngBounds.Builder()
                    polylinePoints.forEach { boundsBuilder.include(it) }
                    val bounds = boundsBuilder.build()
                    cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            } else if (startCoordinates != null && destCoordinates != null) {
                val startLatLng = LatLng(startCoordinates.split(",")[0].toDouble(), startCoordinates.split(",")[1].toDouble())
                val destLatLng = LatLng(destCoordinates.split(",")[0].toDouble(), destCoordinates.split(",")[1].toDouble())

                Marker(
                    state = MarkerState(position = startLatLng),
                    title = "Start",
                    snippet = "Starting point"
                )
                Marker(
                    state = MarkerState(position = destLatLng),
                    title = "Destination",
                    snippet = "Destination point"
                )

                LaunchedEffect(startLatLng, destLatLng) {
                    val boundsBuilder = LatLngBounds.Builder()
                    boundsBuilder.include(startLatLng)
                    boundsBuilder.include(destLatLng)
                    val bounds = boundsBuilder.build()
                    cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }
        }
        Surface(
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Walking Time: %.2f minutes".format(walkingTime.value),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (busDistance.value > 0) {
                    Text(
                        text = "Bus Travel Time: %.2f minutes".format(busTravelTime.value),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        // Navigation and Zoom buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FloatingActionButton(
                onClick = { cameraPositionState.move(CameraUpdateFactory.scrollBy(0f, -100f)) },
                backgroundColor = Color.White,
                contentColor = Color.Black,
                modifier = Modifier.size(40.dp)
            ) {
                Text("↑", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { cameraPositionState.move(CameraUpdateFactory.scrollBy(-100f, 0f)) },
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("←", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                FloatingActionButton(
                    onClick = { cameraPositionState.move(CameraUpdateFactory.scrollBy(0f, 100f)) },
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("↓", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                FloatingActionButton(
                    onClick = { cameraPositionState.move(CameraUpdateFactory.scrollBy(100f, 0f)) },
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("→", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            FloatingActionButton(
                onClick = { cameraPositionState.move(CameraUpdateFactory.zoomIn()) },
                backgroundColor = Color.White,
                contentColor = Color.Black,
                modifier = Modifier.size(40.dp)
            ) {
                Text("+", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            FloatingActionButton(
                onClick = { cameraPositionState.move(CameraUpdateFactory.zoomOut()) },
                backgroundColor = Color.White,
                contentColor = Color.Black,
                modifier = Modifier.size(40.dp)
            ) {
                Text("-", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

fun placesToLatLng(places: List<Place>): List<LatLng> {
    return places.map { place ->
        LatLng(place.latitude, place.longitude)
    }
}
