package com.example.walkwise

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walkwise.data.LoginViewModel
import com.example.walkwise.data.SharedViewModel
import com.example.walkwise.data.SignUpViewModel
import com.example.walkwise.screens.ContributeScreen
import com.example.walkwise.screens.LocationScreen
import com.example.walkwise.screens.LoginScreen
import com.example.walkwise.screens.MapScreen
import com.example.walkwise.screens.SignUpScreen
import com.example.walkwise.screens.StartScreen
import com.example.walkwise.ui.theme.BlueBackground

enum class WalkWiseScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Login(title = R.string.login),
    SignUp(title = R.string.sign_up),
    Location(title = R.string.location),
    Route(title = R.string.route),
    Map(title = R.string.map),
    Contribute(title = R.string.contribute)
}

@Composable
fun WalkWiseAppBar(
    currentScreen: WalkWiseScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    signUpViewModel: SignUpViewModel,
    loginViewModel: LoginViewModel
) {
    val statusBarHeight = with(LocalDensity.current) { 12.dp.toPx() }
    var mDisplayMenu by remember { mutableStateOf(false) }
    val mContext = LocalContext.current
    TopAppBar(
        title = { Text(stringResource(currentScreen.title), color = Color.White) },
        backgroundColor = BlueBackground,
        modifier = modifier.padding(vertical = statusBarHeight.dp),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                Icon(Icons.Default.MoreVert, "", tint = Color.White)
            }
            DropdownMenu(
                expanded = mDisplayMenu,
                onDismissRequest = { mDisplayMenu = false }
            ) {
                DropdownMenuItem(onClick = {
                    Toast.makeText(mContext, "Home", Toast.LENGTH_SHORT).show()
                    navController.navigate(WalkWiseScreen.Location.name)
                }) {
                    Text(text = "Home")
                }
                DropdownMenuItem(onClick = { Toast.makeText(mContext, "Profile", Toast.LENGTH_SHORT).show() }) {
                    Text(text = "Profile")
                }
                DropdownMenuItem(onClick = { Toast.makeText(mContext, "History", Toast.LENGTH_SHORT).show() }) {
                    Text(text = "History")
                }
                DropdownMenuItem(onClick = { Toast.makeText(mContext, "Help", Toast.LENGTH_SHORT).show() }) {
                    Text(text = "Help")
                }
                DropdownMenuItem(onClick = {
                    if (loginViewModel.current == "ok")
                        loginViewModel.logout()
                    if (signUpViewModel.current == "ok")
                        signUpViewModel.logout()
                }) {
                    Text(text = "Logout")
                }
            }
        }
    )
}

@SuppressLint("NewApi")
@Composable
fun WalkWiseApp(
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = WalkWiseScreen.valueOf(
        backStackEntry?.destination?.route ?: WalkWiseScreen.Start.name
    )
    var loginViewModel: LoginViewModel = viewModel()
    loginViewModel.navController = navController
    var signUpViewModel: SignUpViewModel = viewModel()
    signUpViewModel.navController = navController
    signUpViewModel.loginViewModel = loginViewModel
    var sharedViewModel: SharedViewModel = viewModel()

    Scaffold(
        topBar = {
            WalkWiseAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null &&
                        currentScreen != WalkWiseScreen.Login && currentScreen != WalkWiseScreen.SignUp
                        && currentScreen != WalkWiseScreen.Location,
                navigateUp = {
                    navController.navigateUp()
                             },
                navController = navController,
                signUpViewModel = signUpViewModel,
                loginViewModel = loginViewModel
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = WalkWiseScreen.Start.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = WalkWiseScreen.Start.name) {
                StartScreen(
                    onSignUpButtonClicked = {
                        navController.navigate(WalkWiseScreen.SignUp.name)
                    },
                    onLoginButtonClicked = {
                        navController.navigate(WalkWiseScreen.Login.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            composable(route = WalkWiseScreen.Login.name) {
                LoginScreen(
                    onLoginButtonClicked = {
                        navController.navigate(WalkWiseScreen.Location.name)
                    },
                    onSignUpButtonClicked = {
                        navController.navigate(WalkWiseScreen.SignUp.name)
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    loginViewModel
                )
            }

            composable(route = WalkWiseScreen.SignUp.name) {
                SignUpScreen(
                    onLoginButtonClicked = {
                        navController.navigate(WalkWiseScreen.Login.name)
                    },
                    onSignUpButtonClicked = {
                        navController.navigate(WalkWiseScreen.Location.name)
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    signUpViewModel = signUpViewModel,

                )
            }

            composable(route = WalkWiseScreen.Location.name) {
                LocationScreen(
                    onSearchRoutesButtonClicked = {
                        navController.navigate(WalkWiseScreen.Map.name)
                    },
                    onContributeButtonClicked = {
                        navController.navigate(WalkWiseScreen.Contribute.name)
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    sharedViewModel = sharedViewModel
                )
            }

            composable(route = WalkWiseScreen.Map.name) {
                MapScreen(
                    modifier = Modifier
                        .fillMaxSize(),
                    sharedViewModel = sharedViewModel,
                    LocalContext.current
                )
            }

            composable(route = WalkWiseScreen.Contribute.name) {
                ContributeScreen(
                    onSaveButtonClicked = {
                        navController.navigate(WalkWiseScreen.Location.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}