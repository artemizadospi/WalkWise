package com.example.walkwise.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.walkwise.R
import com.example.walkwise.network.MapsService
import com.example.walkwise.ui.theme.BlueBackground
import com.example.walkwise.ui.theme.WalkWiseTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader

@Composable
fun ContributeScreen(
    onSaveButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var map by remember { mutableStateOf<GoogleMap?>(null) }
    var startLocation by remember { mutableStateOf<LatLng?>(null) }
    var endLocation by remember { mutableStateOf<LatLng?>(null) }
    var walkingTime by remember { mutableStateOf("") }
    var walkingDistance by remember { mutableStateOf("") }
    var busTime by remember { mutableStateOf("") }
    var busDistance by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val initialLocation = LatLng(44.438868, 26.049560) // Facultatea de Automatica din Bucuresti, Romania

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
                text = "CONTRIBUTE",
                style = MaterialTheme.typography.h1,
                fontSize = 40.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                GoogleMapComposable(
                    initialLocation = initialLocation,
                    onMapReady = { googleMap ->
                        map = googleMap
                        googleMap.uiSettings.isScrollGesturesEnabled = true
                        googleMap.uiSettings.isZoomGesturesEnabled = true
                        googleMap.uiSettings.isRotateGesturesEnabled = true
                        googleMap.uiSettings.isTiltGesturesEnabled = true
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15f))
                        map?.setOnMapClickListener { latLng ->
                            if (startLocation == null) {
                                startLocation = latLng
                                map?.addMarker(MarkerOptions().position(latLng).title("Start Location"))
                            } else if (endLocation == null) {
                                endLocation = latLng
                                map?.addMarker(MarkerOptions().position(latLng).title("End Location"))
                            }
                        }
                    }
                )

                Column(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = { map?.animateCamera(CameraUpdateFactory.zoomIn()) },
                        backgroundColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                    FloatingActionButton(
                        onClick = { map?.animateCamera(CameraUpdateFactory.zoomOut()) },
                        backgroundColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold)
                    }
                }

                // Directional controls
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = { map?.animateCamera(CameraUpdateFactory.scrollBy(0f, -100f)) },
                        backgroundColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape)
                    ) {
                        Text("↑", fontWeight = FontWeight.Bold)
                    }
                    FloatingActionButton(
                        onClick = { map?.animateCamera(CameraUpdateFactory.scrollBy(0f, 100f)) },
                        backgroundColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape)
                    ) {
                        Text("↓", fontWeight = FontWeight.Bold)
                    }
                    FloatingActionButton(
                        onClick = { map?.animateCamera(CameraUpdateFactory.scrollBy(-100f, 0f)) },
                        backgroundColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape)
                    ) {
                        Text("←", fontWeight = FontWeight.Bold)
                    }
                    FloatingActionButton(
                        onClick = { map?.animateCamera(CameraUpdateFactory.scrollBy(100f, 0f)) },
                        backgroundColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape)
                    ) {
                        Text("→", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (startLocation != null && endLocation != null) {
                        coroutineScope.launch {
                            val startLatLng = "${startLocation!!.latitude},${startLocation!!.longitude}"
                            val endLatLng = "${endLocation!!.latitude},${endLocation!!.longitude}"

                            MapsService(context).getWalkingAndBusDetails(startLatLng, endLatLng) { walkTime, walkDist, busTravelTime, busTravelDist ->
                                walkingTime = walkTime.toString()
                                walkingDistance = walkDist.toString()
                                busTime = busTravelTime.toString()
                                busDistance = busTravelDist.toString()

                                val dataList = listOf(
                                    startLocation!!.latitude.toString(),
                                    startLocation!!.longitude.toString(),
                                    endLocation!!.latitude.toString(),
                                    endLocation!!.longitude.toString(),
                                    walkingTime,
                                    walkingDistance,
                                    busTime,
                                    busDistance
                                )
                                saveDataToCSV(context, dataList)

                                // Print the contents of the CSV file to the log
                                val stringBuilder = StringBuilder()
                                try {
                                    val file = File(context.filesDir, "model/travel_time_data.csv")
                                    val inputStream = FileInputStream(file)
                                    val inputStreamReader = InputStreamReader(inputStream)
                                    val bufferedReader = BufferedReader(inputStreamReader)
                                    val headerLine = bufferedReader.readLine()
                                    stringBuilder.append(headerLine).append("\n")
                                    var line: String? = bufferedReader.readLine()
                                    while (line != null) {
                                        stringBuilder.append(line).append("\n")
                                        line = bufferedReader.readLine()
                                    }
                                    inputStream.close()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                Log.d("mytag", stringBuilder.toString())

                                // Reset the map markers but keep the walking and bus details
                                startLocation = null
                                endLocation = null
                                map?.clear()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please select both start and end locations", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.size(width = 300.dp, height = 50.dp)
            ) {
                Text(
                    "FETCH & SAVE DATA",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Walking Time: $walkingTime seconds",
                color = Color.White
            )
            Text(
                text = "Walking Distance: $walkingDistance meters",
                color = Color.White
            )
            Text(
                text = "Bus Travel Time: $busTime seconds",
                color = Color.White
            )
            Text(
                text = "Bus Travel Distance: $busDistance meters",
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

fun saveDataToCSV(context: Context, dataList: List<String>) {
    val folder = File(context.filesDir, "model")
    if (!folder.exists()) {
        folder.mkdirs()
    }

    val fileName = "travel_time_data.csv"
    val file = File(folder, fileName)

    try {
        val fileExists = file.exists()
        val writer = FileWriter(file, true)

        if (!fileExists) {
            Log.d("mytag", "header appended")
            writer.append("start_latitude,start_longitude,end_latitude,end_longitude,walking_time_seconds,distance,public_transport_travel_time,public_transport_distance\n")
        }

        // Write data to CSV
        writer.append("${dataList.joinToString(separator = ",")}\n")

        writer.flush()
        writer.close()

        // Optionally, you can provide feedback to the user that data is saved successfully
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle IOException
    }
}

@Composable
fun GoogleMapComposable(
    initialLocation: LatLng,
    onMapReady: (GoogleMap) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                getMapAsync { googleMap ->
                    googleMap.uiSettings.isScrollGesturesEnabled = true
                    googleMap.uiSettings.isZoomGesturesEnabled = true
                    googleMap.uiSettings.isRotateGesturesEnabled = true
                    googleMap.uiSettings.isTiltGesturesEnabled = true
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15f))
                    onMapReady(googleMap)
                }
            }
        },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ContributeOrderPreview() {
    WalkWiseTheme {
        ContributeScreen(
            onSaveButtonClicked = {},
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
