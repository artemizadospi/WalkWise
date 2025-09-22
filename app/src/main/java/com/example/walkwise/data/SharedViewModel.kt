package com.example.walkwise.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var myLocation by mutableStateOf("")
    var destination by mutableStateOf("")
    var startCoordinates by mutableStateOf<String?>(null)
    var destCoordinates by mutableStateOf<String?>(null)

    fun setStartLocation(myLocation: String) {
        this.myLocation = myLocation
        this.startCoordinates = null
    }

    fun setDestLocation(destination: String) {
        this.destination = destination
        this.destCoordinates = null
    }

    fun setStartCoordinates(latitude: Double, longitude: Double) {
        this.startCoordinates = "$latitude,$longitude"
        this.myLocation = ""
    }

    fun setDestCoordinates(latitude: Double, longitude: Double) {
        this.destCoordinates = "$latitude,$longitude"
        this.destination = ""
    }
}
