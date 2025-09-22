package com.example.walkwise.data.rules

object Validator {

    fun validateUsername(username: String): ValidationResult{
        return ValidationResult(
            (!username.isNullOrEmpty())
        )
    }

    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            (!email.isNullOrEmpty())
        )
    }

    fun validatePassword(password: String): ValidationResult {
        return ValidationResult(
            (!password.isNullOrEmpty())
        )
    }

    fun validateConfirmationPassword(confirmationPassword: String): ValidationResult {
        return ValidationResult(
            (!confirmationPassword.isNullOrEmpty())
        )
    }
}

data class ValidationResult(
    val status: Boolean = false
)