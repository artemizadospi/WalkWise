package com.example.walkwise.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.walkwise.WalkWiseScreen
import com.example.walkwise.data.rules.Validator
import com.example.walkwise.graph.RouteGraph
import com.example.walkwise.model.FileDownloadCallback
import com.example.walkwise.model.FileViewModel
import com.example.walkwise.model.ModelVersionManager
import com.example.walkwise.modeltraining.ModelTrainer
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LoginViewModel : ViewModel() {
    var loginUIState = mutableStateOf(LoginUIState())
    var allValidationsPassed = mutableStateOf(false)
    lateinit var navController: NavController
    var current = ""
    var loginInProgress = mutableStateOf(false)
    lateinit var context: Context

    fun onEvent(event: LoginUIEvent) {
        when(event) {
            is LoginUIEvent.EmailChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    email = event.email
                )
            }
            is LoginUIEvent.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password
                )
            }
            is LoginUIEvent.LoginButtonClicked -> {
                login()
            }
        }
        validateDataWithRules()
    }

    private fun login() {
        val email = loginUIState.value.email
        val password = loginUIState.value.password

        loginInProgress.value = true
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                loginInProgress.value = false
                if(it.isSuccessful) {
                    val file1 = File(context.filesDir, "model/averaged_model.ckpt")

                    if (file1.exists()) {
                        val deleted = file1.delete()
                        if (deleted) {
                            Log.d("mytag", "File deleted successfully")
                        } else {
                            Log.d("mytag", "Failed to delete the file")
                        }
                    } else {
                        Log.d("mytag", "File does not exist")
                    }
                    val fileViewModel = FileViewModel()
                    Toast.makeText(context, "Update started", Toast.LENGTH_LONG).show()
                    fileViewModel.getFile(context, object : FileDownloadCallback {
                        override fun onFileDownloaded(modelVersion: Int) {
                            val file = File(context.filesDir, "model/averaged_model.ckpt")
                            Log.d("mytag", ModelTrainer.getInstance(context).isCheckpointSaved(file.absolutePath).toString())
                            ModelTrainer.getInstance(context).restoreModel(file.absolutePath)
                            val versionManager = ModelVersionManager.getInstance(context)
                            versionManager.modelVersion = modelVersion
                            Log.d("mytag", "Model version: " + ModelVersionManager.getInstance(context).modelVersion)

                            CoroutineScope(Dispatchers.Main).launch {
//                                Toast.makeText(context, "Update started", Toast.LENGTH_LONG).show()
                                withContext(Dispatchers.IO) {
                                    RouteGraph.graph.updateCosts(context)
                                }
                            }
                            Toast.makeText(context, "Completed update", Toast.LENGTH_LONG).show()
                        }

                        override fun onError(error: Exception) {
                            Log.e("mytag", "File download failed", error)
                            CoroutineScope(Dispatchers.Main).launch {
//                                Toast.makeText(context, "Update started", Toast.LENGTH_LONG).show()
                                withContext(Dispatchers.IO) {
                                    RouteGraph.graph.updateCosts(context)
                                }
                            }
                            Toast.makeText(context, "Completed update", Toast.LENGTH_LONG).show()
                        }
                    })
//                    RouteGraph.graph.updateCosts(context)
                    navController.navigate(WalkWiseScreen.Location.name)
                    current = "ok"
                }
            }
            .addOnFailureListener {
                it.message?.let { it1 -> Log.d("MyTag", it1) }
            }
    }

    private fun validateDataWithRules() {
        val email = Validator.validateEmail(
            email = loginUIState.value.email
        )
        val password = Validator.validatePassword(
            password = loginUIState.value.password
        )

        loginUIState.value = loginUIState.value.copy(
            emailError = email.status,
            passwordError = password.status
        )

        allValidationsPassed.value = email.status && password.status
    }

    fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                current = ""
                onEvent(LoginUIEvent.EmailChanged(""))
                onEvent(LoginUIEvent.PasswordChanged(""))
                navController.navigate(WalkWiseScreen.Login.name)
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
    }

}