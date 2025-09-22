package com.example.walkwise.data

data class SignUpUIState(
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var confirmationPassword: String = "",

    var usernameError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false,
    var confirmationPasswordError: Boolean = false
)