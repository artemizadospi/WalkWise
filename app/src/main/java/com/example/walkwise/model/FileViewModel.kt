package com.example.walkwise.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(
    private val fileRepository: FileRepository = FileRepository()
): ViewModel() {
    fun uploadFile(file: File) {
        viewModelScope.launch {
            fileRepository.uploadFile(file)
        }
    }

    fun getFile(context: Context, callback: FileDownloadCallback) {
        viewModelScope.launch{
            fileRepository.getFile(context, "averaged_model.ckpt", callback)
        }
    }
}