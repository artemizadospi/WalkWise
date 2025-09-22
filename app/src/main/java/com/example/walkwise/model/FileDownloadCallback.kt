package com.example.walkwise.model

interface FileDownloadCallback {
    fun onFileDownloaded(modelVersion: Int)
    fun onError(error: Exception)
}