package com.example.walkwise.model

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkwise.modeltraining.ModelTrainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FileDownloadBackgroundViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun startBackgroundTask(fileViewModel: FileViewModel, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val file = File(context.filesDir, "model/travel_time_data.csv")
                if (file.exists()) {
                    val numberOfRows = file.readLines().size - 1
                    Log.d("mytag", numberOfRows.toString())
                    if (numberOfRows >= 5) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Training started", Toast.LENGTH_LONG).show()
                        }
                        ModelTrainer.getInstance(context).trainModel()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Training ended", Toast.LENGTH_LONG).show()
                        }
                        val checkpointFile = ModelTrainer.getInstance(context).save()
                        if (checkpointFile == null) {
                            Log.d("mytag", "checkpointFile is null")
                        } else {
                            if (ModelTrainer.getInstance(context).isCheckpointSaved(checkpointFile)) {
                                Log.d("mytag", "checkpointFile is saved")
                                val checkpoint = File(checkpointFile)
                                fileViewModel.uploadFile(checkpoint)
                                val file = File(context.filesDir, "model/travel_time_data.csv")
                                file.delete()
                            } else {
                                Log.d("mytag", "checkpointFile is not here")
                            }
                        }
                    }
                }
                delay(5000)
            }
        }
    }

//    private suspend fun performBackgroundTask(fileViewModel: FileViewModel, context: Context) {
//        fileViewModel.getFile(context)
//    }
}
