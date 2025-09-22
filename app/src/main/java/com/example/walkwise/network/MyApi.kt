package com.example.walkwise.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MyApi {

    @GET("communication")
    fun getFile(@Query("filename") fileName: String): Call<ResponseBody>

    @Multipart
    @POST("communication")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    )

    companion object {
        private const val BASE_URL = "http://192.168.224.1:8080"
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build()
                .create(MyApi::class.java)
        }
    }
}