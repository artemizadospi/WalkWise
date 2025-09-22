package com.example.walkwise.screens

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walkwise.R
import com.example.walkwise.components.ButtonComponent
import com.example.walkwise.components.DividerTextComponent
import com.example.walkwise.components.PasswordFieldComponent
import com.example.walkwise.components.TextFieldComponent
import com.example.walkwise.data.SignUpViewModel
import com.example.walkwise.data.SignUpUIEvent
import com.example.walkwise.ui.theme.BlueBackground
import com.example.walkwise.ui.theme.WalkWiseTheme
import androidx.lifecycle.lifecycleScope
import com.example.walkwise.WalkWiseScreen
import com.example.walkwise.data.GoogleAuthUIClient
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    onSignUpButtonClicked: () -> Unit,
    onLoginButtonClicked: () -> Unit,
    signUpViewModel: SignUpViewModel,
    modifier: Modifier = Modifier
) {
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
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.h1,
                    fontSize = 40.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Create your account",
                    style = MaterialTheme.typography.h1,
                    fontSize = 15.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextFieldComponent(placeholderValue = "Username", Icons.Filled.Person,
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.UsernameChanged(it))
                    }, signUpViewModel.signUpUIState.value.usernameError)
                Spacer(modifier = Modifier.height(2.dp))
                TextFieldComponent(placeholderValue = "Email", Icons.Filled.Email,
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.EmailChanged(it))
                    }, signUpViewModel.signUpUIState.value.emailError)
                Spacer(modifier = Modifier.height(2.dp))
                PasswordFieldComponent(placeholderValue = "Password", Icons.Filled.Lock,
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.PasswordChanged(it))
                    }, signUpViewModel.signUpUIState.value.passwordError)
                Spacer(modifier = Modifier.height(2.dp))
                PasswordFieldComponent(placeholderValue = "Confirm Password", Icons.Filled.Lock,
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.ConfirmationPasswordChanged(it))
                    }, signUpViewModel.signUpUIState.value.confirmationPasswordError)
                Spacer(modifier = Modifier.height(5.dp))
                Spacer(modifier = Modifier.height(2.dp))
                ButtonComponent(textValue = "Sign Up", buttonColor = Color.White, textColor = Color.Black, onSignUpButtonClicked,
                    action= {
                        signUpViewModel.onEvent(SignUpUIEvent.SignUpButtonClicked)
                    }, isEnabled = signUpViewModel.allValidationsPassed.value)
                DividerTextComponent()
                val context1 = LocalContext.current as LifecycleOwner
                val context2 = LocalContext.current
                val googleAuthUiClient by lazy {
                    GoogleAuthUIClient(
                        context = context2,
                        oneTapClient = Identity.getSignInClient(context2)
                    )
                }
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if(result.resultCode == RESULT_OK) {
                            context1.lifecycleScope.launch {
                                val signInResult = googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                signInResult.data?.email?.let {
                                    SignUpUIEvent.EmailChanged(
                                        it
                                    )
                                }?.let { signUpViewModel.onEvent(it) }
                                signInResult.data?.username?.let {
                                    SignUpUIEvent.UsernameChanged(
                                        it
                                    )
                                }?.let { signUpViewModel.onEvent(it) }
                                signInResult.data?.userId?.let {
                                    SignUpUIEvent.PasswordChanged(
                                        it
                                    )
                                }?.let { signUpViewModel.onEvent(it) }
                                signInResult.data?.userId?.let {
                                    SignUpUIEvent.ConfirmationPasswordChanged(
                                        it
                                    )
                                }?.let { signUpViewModel.onEvent(it) }
                                signUpViewModel.navController.navigate(WalkWiseScreen.Location.name)
                            }
                        }
                    }
                )
                Button(
                    onClick = {
                        context1.lifecycleScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }

                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    shape = RoundedCornerShape(30.dp),
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier.size(width = 300.dp, height = 50.dp)
                ) {
                    Text(
                        "Sign In with Google",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.h1,
                        fontSize = 15.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.SansSerif
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { onLoginButtonClicked() }) {
                            Text(
                                "Login",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
        if (signUpViewModel.signUpInProgress.value) {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpOrderPreview() {
    WalkWiseTheme {
        SignUpScreen(
            onSignUpButtonClicked = {},
            onLoginButtonClicked = {},
            modifier = Modifier
                .fillMaxSize(),
            signUpViewModel = viewModel()
        )
    }
}