package com.example.walkwise.network

import android.content.Context
import android.util.Log
import com.example.walkwise.graph.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MapsService(private val context: Context) {

    private val apiKey: String = "AIzaSyAPELVMxbOHTK7G70PwORKVqbgjFiFU8Qk"

    fun getWalkingAndBusDetails(
        origin: String,
        destination: String,
        callback: (walkingTime: Int, walkingDistance: Int, busTime: Int, busDistance: Int) -> Unit
    ) {
        RetrofitClient.api.getDirections(origin, destination, "walking", apiKey, false)
            .enqueue(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val walkingLeg = response.body()!!.routes[0].legs[0]
                        val walkingDistance = walkingLeg.distance.value
                        val walkingTime = walkingLeg.duration.value
                        getBusDetails(origin, destination, walkingTime, walkingDistance, callback)
                    } else {
                        callback(0, 0, 0, 0)
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.e("MapsService", "Failed to get walking distance and time", t)
                    callback(0, 0, 0, 0)
                }
            })
    }

    private fun getBusDetails(
        origin: String,
        destination: String,
        walkingTime: Int,
        walkingDistance: Int,
        callback: (walkingTime: Int, walkingDistance: Int, busTime: Int, busDistance: Int) -> Unit
    ) {
        RetrofitClient.api.getDirections(origin, destination, "transit", apiKey, true)
            .enqueue(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("MapsService", "response is good")
                        val routes = response.body()!!.routes
                        var busDistance = 0
                        var busTime = walkingTime  // Default to walking time if no suitable route found
                        var suitableRouteFound = false

                        for (route in routes) {
                            var tempBusDistance = 0
                            var busFound = false

                            for (leg in route.legs) {
                                for (step in leg.steps) {
                                    val travelMode = step.travel_mode
                                    val vehicleType = step.transit_details?.line?.vehicle?.type?.lowercase(Locale.ROOT)

                                    Log.d("MapsService", "Step travel mode: $travelMode, vehicle type: $vehicleType")

                                    if (travelMode == "TRANSIT" && vehicleType != null && "bus" in vehicleType) {
                                        busFound = true
                                        tempBusDistance += step.distance.value
                                        Log.d("MapsService", "Bus step found: distance = ${step.distance.value}")
                                    } else if (travelMode == "TRANSIT" && vehicleType != null && "bus" !in vehicleType) {
                                        busFound = false
                                        Log.d("MapsService", "Non-bus transit step found: breaking")
                                        break
                                    }
                                }
                                if (!busFound) {
                                    Log.d("MapsService", "No bus steps in this leg: breaking")
                                    break
                                }
                            }

                            if (busFound) {
                                busDistance = tempBusDistance
                                busTime = route.legs[0].duration.value  // Use the route duration for bus time
                                suitableRouteFound = true
                                Log.d("MapsService", "Suitable route found with bus distance = $busDistance, bus time = $busTime")
                                break
                            }
                        }

                        if (suitableRouteFound) {
                            callback(walkingTime, walkingDistance, busTime, busDistance)
                        } else {
                            Log.d("MapsService", "No suitable route found with bus transit")
                            callback(walkingTime, walkingDistance, walkingTime, 0)
                        }
                    } else {
                        Log.d("MapsService", "response is not good: ${response.errorBody()?.string()}")
                        callback(walkingTime, walkingDistance, walkingTime, 0)
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.e("MapsService", "Failed to get bus distance and time", t)
                    callback(walkingTime, walkingDistance, walkingTime, 0)
                }
            })
    }
}
