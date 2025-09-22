package com.example.walkwise.screens

import android.location.Geocoder
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walkwise.R
import com.example.walkwise.components.DividerTextComponent
import com.example.walkwise.data.SharedViewModel
import com.example.walkwise.graph.Place
import com.example.walkwise.graph.RouteGraph
import com.example.walkwise.ui.theme.BlueBackground
import com.example.walkwise.ui.theme.WalkWiseTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import java.util.Locale
import kotlin.math.sqrt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationScreen(
    onSearchRoutesButtonClicked: () -> Unit,
    onContributeButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel
) {
    var myLocationValue by remember { mutableStateOf("") }
    var locationValue by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graph = RouteGraph.graph

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueBackground),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "ROUTES",
                style = MaterialTheme.typography.h1,
                fontSize = 40.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(50.dp))
            OutlinedTextField(
                value = myLocationValue,
                onValueChange = { newValue ->
                    myLocationValue = newValue
                    sharedViewModel.setStartLocation(newValue)
                },
                placeholder = {
                    Text(
                        "MyLocation",
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Black
                    )
                },
                modifier = Modifier.width(350.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    backgroundColor = Color.White,
                    textColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                ),
                leadingIcon = {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.Black)
                },
            )
            Spacer(modifier = Modifier.height(2.dp))
            OutlinedTextField(
                value = locationValue,
                onValueChange = { newValue ->
                    locationValue = newValue
                    sharedViewModel.setDestLocation(newValue)
                },
                placeholder = {
                    Text(
                        "Search",
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Black
                    )
                },
                modifier = Modifier.width(350.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    backgroundColor = Color.White,
                    textColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                ),
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Black)
                },
            )
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            if (myLocationValue.isBlank() || locationValue.isBlank()) {
                                Toast.makeText(context, "Please enter both locations.", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            val threshold = 0.01 // Define your threshold here

                            val startPlace = graph.adjacencyMap.keys.find { it.name.equals(myLocationValue, ignoreCase = true) }
                            val destPlace = graph.adjacencyMap.keys.find { it.name.equals(locationValue, ignoreCase = true) }

                            val geocoder = Geocoder(context, Locale.getDefault())
                            val startAddress = if (startPlace == null) withTimeoutOrNull(5000) { withContext(Dispatchers.IO) { geocoder.getFromLocationName(myLocationValue, 1) } } else null
                            val destAddress = if (destPlace == null) withTimeoutOrNull(5000) { withContext(Dispatchers.IO) { geocoder.getFromLocationName(locationValue, 1) } } else null

                            if (startAddress == null && startPlace == null || destAddress == null && destPlace == null) {
                                Toast.makeText(context, "Failed to fetch coordinates or find close points for locations.", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            val startCoords = startAddress?.firstOrNull()
                            val destCoords = destAddress?.firstOrNull()

                            val closestStartPlace = startCoords?.let { findClosestPlaceOrDefault(graph, Place("Start", it.latitude, it.longitude), threshold) }
                            val closestDestPlace = destCoords?.let { findClosestPlaceOrDefault(graph, Place("Destination", it.latitude, it.longitude), threshold) }

                            val useStartCoords = startCoords != null && closestStartPlace == null
                            val useDestCoords = destCoords != null && closestDestPlace == null

                            if (startPlace != null) {
                                sharedViewModel.setStartLocation(startPlace.name)
                            } else if (closestStartPlace != null) {
                                sharedViewModel.setStartLocation(closestStartPlace.name)
                            } else if (useStartCoords) {
                                if (startCoords != null) {
                                    sharedViewModel.setStartCoordinates(startCoords.latitude, startCoords.longitude)
                                }
                            }

                            if (destPlace != null) {
                                sharedViewModel.setDestLocation(destPlace.name)
                            } else if (closestDestPlace != null) {
                                sharedViewModel.setDestLocation(closestDestPlace.name)
                            } else if (useDestCoords) {
                                if (destCoords != null) {
                                    sharedViewModel.setDestCoordinates(destCoords.latitude, destCoords.longitude)
                                }
                            }

                            // If either start or destination uses coordinates, set both to use coordinates.
                            if (useStartCoords || useDestCoords) {
                                if (startCoords != null && destCoords != null) {
                                    sharedViewModel.setStartCoordinates(startCoords.latitude, startCoords.longitude)
                                    sharedViewModel.setDestCoordinates(destCoords.latitude, destCoords.longitude)
                                }
                            }

                            if (startPlace != null || closestStartPlace != null || useStartCoords) {
                                if (destPlace != null || closestDestPlace != null || useDestCoords) {
                                    onSearchRoutesButtonClicked()
                                } else {
                                    Toast.makeText(context, "No close points found in the graph and could not retrieve coordinates for the destination.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "No close points found in the graph and could not retrieve coordinates for the start location.", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: IOException) {
                            Toast.makeText(context, "Geocoding failed, please try again.", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.size(width = 300.dp, height = 50.dp)
            ) {
                Text(
                    "SEARCH ROUTES",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                )
            }
            DividerTextComponent()
            Button(
                onClick = {
                    onContributeButtonClicked()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.size(width = 300.dp, height = 50.dp)
            ) {
                Text(
                    "CONTRIBUTE",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

fun findClosestPlaceOrDefault(graph: RouteGraph, place: Place, threshold: Double): Place? {
    val closestPlace = graph.adjacencyMap.keys.minByOrNull {
        distance(it.latitude, it.longitude, place.latitude, place.longitude)
    }
    val minDistance = closestPlace?.let { distance(it.latitude, it.longitude, place.latitude, place.longitude) }
    Log.d("LocationScreen", "Closest place found: $closestPlace for input place: $place with distance: $minDistance")
    return if (minDistance != null && minDistance <= threshold) closestPlace else null
}

fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val latDiff = lat1 - lat2
    val lonDiff = lon1 - lon2
    return sqrt(latDiff * latDiff + lonDiff * lonDiff)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun LocationOrderPreview() {
    WalkWiseTheme {
        LocationScreen(
            onSearchRoutesButtonClicked = {},
            onContributeButtonClicked = {},
            modifier = Modifier.fillMaxSize(),
            sharedViewModel = viewModel()
        )
    }
}
