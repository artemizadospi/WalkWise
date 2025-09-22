package com.example.walkwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.walkwise.model.FileDownloadBackgroundViewModel
import com.example.walkwise.model.FileViewModel
import com.example.walkwise.ui.theme.WalkWiseTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: FileDownloadBackgroundViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FileDownloadBackgroundViewModel::class.java)
        val fileViewModel = FileViewModel()
        val context = this

        viewModel.startBackgroundTask(fileViewModel, context)
        setContent {
            WalkWiseTheme {
                WalkWiseApp()
            }
        }
    }
}
