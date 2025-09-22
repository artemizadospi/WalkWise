package com.example.walkwise.data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.walkwise.WalkWiseScreen
import com.example.walkwise.data.rules.Validator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class SignUpViewModel : ViewModel() {
    var signUpUIState = mutableStateOf(SignUpUIState())
    var allValidationsPassed = mutableStateOf(false)
    lateinit var navController: NavController
    lateinit var loginViewModel: LoginViewModel
    var current = ""
    var signUpInProgress = mutableStateOf(false)

    fun onEvent(event: SignUpUIEvent) {
//        validateDataWithRules()
        when(event) {
            is SignUpUIEvent.UsernameChanged -> {
                signUpUIState.value = signUpUIState.value.copy(
                    username = event.username
                )
            }
            is SignUpUIEvent.EmailChanged -> {
                signUpUIState.value = signUpUIState.value.copy(
                    email = event.email
                )
            }
            is SignUpUIEvent.PasswordChanged -> {
                signUpUIState.value = signUpUIState.value.copy(
                    password = event.password
                )
            }
            is SignUpUIEvent.ConfirmationPasswordChanged -> {
                signUpUIState.value = signUpUIState.value.copy(
                    confirmationPassword = event.confirmationPassword
                )
            }

            is SignUpUIEvent.SignUpButtonClicked -> {
                signUp()
            }
        }
        validateDataWithRules()
    }

    private fun signUp() {
        createUserInFirebase(
            email = signUpUIState.value.email,
            password = signUpUIState.value.password
        )
    }

    private fun validateDataWithRules() {
        val username = Validator.validateUsername(
            username = signUpUIState.value.username
        )
        val email = Validator.validateEmail(
            email = signUpUIState.value.email
        )
        val password = Validator.validatePassword(
            password = signUpUIState.value.password
        )
        val confirmationPassword = Validator.validateConfirmationPassword(
            confirmationPassword = signUpUIState.value.confirmationPassword
        )

        signUpUIState.value = signUpUIState.value.copy(
            usernameError = username.status,
            emailError = email.status,
            passwordError = password.status,
            confirmationPasswordError = confirmationPassword.status
        )

        allValidationsPassed.value = username.status && email.status && password.status && confirmationPassword.status
    }

    private fun createUserInFirebase(email: String, password: String) {
        signUpInProgress.value = true
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                signUpInProgress.value = false
                if(it.isSuccessful) {
                    navController.navigate(WalkWiseScreen.Location.name)
                    current = "ok"
                }
            }
            .addOnFailureListener {
                it.message?.let { it1 -> Log.d("MyTag", it1) }
            }
    }

    fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()
        val authStateListener = AuthStateListener {
            if (it.currentUser == null) {
                onEvent(SignUpUIEvent.EmailChanged(""))
                onEvent(SignUpUIEvent.PasswordChanged(""))
                onEvent(SignUpUIEvent.ConfirmationPasswordChanged(""))
                onEvent(SignUpUIEvent.UsernameChanged(""))
                current = ""
                navController.navigate(WalkWiseScreen.Login.name)
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
    }
}