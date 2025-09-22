package com.example.walkwise.model

import android.content.Context
import android.util.Log
import com.example.walkwise.network.MyApi
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class FileRepository {
    suspend fun uploadFile(file: File): Boolean {
        return try {
            MyApi.instance.uploadFile(
                file = MultipartBody.Part
                    .createFormData(
                        "file",
                        file.name,
                        file.asRequestBody()
                    )
            )
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun getFile(context: Context, fileName: String, callback: FileDownloadCallback) {
        val call = MyApi.instance.getFile(fileName)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        // Get the modelVersion from the headers
                        val modelVersion = response.headers()["modelVersion"]?.toIntOrNull()

                        if (modelVersion == null) {
                            callback.onError(IOException("Model version header missing or invalid"))
                            return
                        }

                        // Get the file content
                        val fileContent = response.body()!!.bytes()

                        // Save the file to internal storage
                        val modelDir = File(context.filesDir, "model")
                        if (!modelDir.exists()) {
                            modelDir.mkdirs()
                        }

                        val internalFile = File(modelDir, fileName)
                        FileOutputStream(internalFile).use { fos ->
                            fos.write(fileContent)
                        }
                        Log.d("mytag", "File saved to internal storage: ${internalFile.absolutePath}")
                        Log.d("mytag", "Model version: $modelVersion")

                        // Notify callback that file is downloaded and pass the model version
                        callback.onFileDownloaded(modelVersion)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.e("mytag", "Error saving or copying file: ${e.message}")
                        callback.onError(e)
                    }
                } else {
                    // Handle the case where body is null or response is not successful
                    Log.e("mytag", "Download failed or empty body")
                    callback.onError(IOException("Download failed or empty body"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("mytag", "Request failed with exception: ${t.message}")
                callback.onError(Exception(t))
            }
        })
    }
}