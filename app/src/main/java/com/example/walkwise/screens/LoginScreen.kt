package com.example.walkwise.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walkwise.R
import com.example.walkwise.components.ButtonComponent
import com.example.walkwise.components.PasswordFieldComponent
import com.example.walkwise.components.TextFieldComponent
import com.example.walkwise.data.LoginUIEvent
import com.example.walkwise.data.LoginViewModel
import com.example.walkwise.ui.theme.BlueBackground
import com.example.walkwise.ui.theme.WalkWiseTheme

@Composable
fun LoginScreen(
    onLoginButtonClicked: () -> Unit,
    onSignUpButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel
) {
    loginViewModel.context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.h1,
                    fontSize = 40.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Enter your credential to login",
                    style = MaterialTheme.typography.h1,
                    fontSize = 15.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.height(50.dp))
                TextFieldComponent(placeholderValue = "Email", icon = Icons.Filled.Email,
                    onTextSelected = {
                        loginViewModel.onEvent(LoginUIEvent.EmailChanged(it))
                    },
                    errorStatus = loginViewModel.loginUIState.value.emailError)
                Spacer(modifier = Modifier.height(2.dp))
                PasswordFieldComponent(placeholderValue = "Password", icon = Icons.Filled.Lock,
                    onTextSelected = {
                        loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it))
                    },
                    errorStatus = loginViewModel.loginUIState.value.passwordError)
                Spacer(modifier = Modifier.height(2.dp))
                ButtonComponent(textValue = "Login", buttonColor = Color.White, textColor = Color.Black, onLoginButtonClicked,
                    action = {
                        loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                    },
                    isEnabled = loginViewModel.allValidationsPassed.value)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.h1,
                        fontSize = 15.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.SansSerif
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { onSignUpButtonClicked() }) {
                            Text(
                                "Sign Up",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(210.dp))
            }
        }
        if (loginViewModel.loginInProgress.value) {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginOrderPreview() {
    WalkWiseTheme {
        LoginScreen(
            onLoginButtonClicked = {},
            onSignUpButtonClicked = {},
            modifier = Modifier
                .fillMaxSize(),
            loginViewModel = viewModel()
        )
    }
}