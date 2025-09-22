package com.example.walkwise.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextFieldComponent(placeholderValue: String, icon: ImageVector,
                       onTextSelected: (String) ->Unit,
                       errorStatus: Boolean = false
) {
    var textValue by remember { mutableStateOf("") }

    OutlinedTextField(
        value = textValue,
        onValueChange = { newValue ->
            textValue = newValue
            onTextSelected(textValue)
        },
        placeholder = {
            Text(
                placeholderValue,
                fontFamily = FontFamily.SansSerif,
                color = Color.Black
            )
        },
        modifier = Modifier
            .width(360.dp),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            backgroundColor = Color.White,
            textColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        maxLines = 1,
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = Color.Black)
        },
        isError = !errorStatus
    )
}

@Composable
fun PasswordFieldComponent(placeholderValue: String, icon: ImageVector,
                           onTextSelected: (String) ->Unit,
                           errorStatus: Boolean = false
) {
    var passwordValue by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = passwordValue,
        onValueChange = { newValue ->
            passwordValue = newValue
            onTextSelected(passwordValue)
        },
        placeholder = {
            Text(
                placeholderValue,
                fontFamily = FontFamily.SansSerif,
                color = Color.Black
            )
        },
        modifier = Modifier
            .width(360.dp),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            backgroundColor = Color.White,
            textColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        maxLines = 1,
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = Color.Black)
        },
        trailingIcon = {
            val iconImage = if(passwordVisible) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(iconImage, contentDescription = null, tint = Color.Black)
            }
        },
        visualTransformation = if(passwordVisible) VisualTransformation.None else
        PasswordVisualTransformation(),
        isError = !errorStatus
    )
}

@Composable
fun ButtonComponent(textValue: String, buttonColor: Color, textColor: Color, onSignUpButtonClicked: () -> Unit,
                    action: () -> Unit, isEnabled: Boolean = false
) {
    Button(
        onClick = {
            action.invoke()
            //onSignUpButtonClicked()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier.size(width = 300.dp, height = 50.dp),
        enabled = isEnabled
    ) {
        Text(
            textValue,
            color = textColor,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DividerTextComponent() {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(25.dp))
        Divider(modifier = Modifier.width(150.dp),
            color = Color.White,
            thickness = 1.dp)

        Text(
            "Or",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Divider(modifier = Modifier.width(150.dp),
            color = Color.White,
            thickness = 1.dp)
    }
}