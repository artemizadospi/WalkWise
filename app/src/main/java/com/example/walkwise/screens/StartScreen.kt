package com.example.walkwise.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walkwise.R
import com.example.walkwise.ui.theme.BlueBackground
import com.example.walkwise.ui.theme.WalkWiseTheme

@Composable
fun StartScreen(
    onSignUpButtonClicked: () -> Unit,
    onLoginButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(BlueBackground),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Image(
                painter = painterResource(R.drawable.start_background),
                contentDescription = null,
                modifier = Modifier.width(400.dp).height(320.dp)
            )
            Text(
                text = "BEGIN YOUR JOURNEY NOW",
                style = MaterialTheme.typography.h1,
                fontSize = 40.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onSignUpButtonClicked() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.size(width = 300.dp, height = 50.dp)
            ) {
                Text(
                    "Sign Up",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onLoginButtonClicked() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.size(width = 300.dp, height = 50.dp)
            ) {
                Text(
                    "Login",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartOrderPreview() {
    WalkWiseTheme {
        StartScreen(
            onSignUpButtonClicked = {},
            onLoginButtonClicked = {},
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

