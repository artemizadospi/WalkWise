package com.example.walkwise.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val steps: List<Step>
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class Step(
    val travel_mode: String,
    val distance: Distance,  // Add this property to capture distance information
    val transit_details: TransitDetails?
)

data class TransitDetails(
    val line: Line
)

data class Line(
    val vehicle: Vehicle
)

data class Vehicle(
    val type: String
)

interface GoogleMapsApi {
    @GET("maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") apiKey: String,
        @Query("alternatives") alternatives: Boolean  // Add the alternatives parameter here
    ): Call<DirectionsResponse>
}