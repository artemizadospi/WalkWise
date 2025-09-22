// In a suitable place in your project (e.g., a singleton object or a utility class)
package com.example.walkwise.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://maps.googleapis.com/"

    val geocodingService: GeocodingService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)
    }
}
