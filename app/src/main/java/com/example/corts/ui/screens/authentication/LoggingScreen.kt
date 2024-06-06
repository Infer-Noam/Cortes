package com.example.corts.ui.screens.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.corts.R
import com.example.corts.ui.animations.loading.DotsPulsing


@Composable
fun LoggingScreen(navController: NavController) {

  //  var currentSubScreen by rememberSaveable { mutableStateOf(LOGGINGSUBSCREEN.MAIN) } // sub screen that is only accessed from logging screen

    val authViewModel: AuthViewModel = hiltViewModel()
    val loading by authViewModel.loading.collectAsStateWithLifecycle()

    val context = LocalContext.current

    if(loading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
            //     .background(
            //         color = Color.White,
            //         shape = RoundedCornerShape(4.dp)
            //     )
        ) {
            DotsPulsing()
        }
    }
    else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
               // .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                    val isDarkTheme = isSystemInDarkTheme()

                    Spacer(modifier = Modifier.weight(16f))

                    SignInButton(onClick = { authViewModel.signInWithGoogle(context) }, isDarkTheme = isDarkTheme, signInMethod = SignInMethod.GOOGLE)

                    Spacer(modifier = Modifier.weight(1f))

                    SignInButton(onClick = { navController.navigate("email_logging") }, isDarkTheme = isDarkTheme, signInMethod = SignInMethod.EMAIL)

                    Spacer(modifier = Modifier.weight(4f))
        }
    }
}

@Composable
fun SignInButton(onClick: () -> Unit, isDarkTheme: Boolean, signInMethod: SignInMethod) {
    val backgroundColor = if (isDarkTheme) Color(0xFF131314) else Color(0xFFFFFFFF)
    val contentColor = if (isDarkTheme) Color(0xFFE3E3E3) else Color(0xFF1F1F1F)
    val borderColor = if (isDarkTheme) Color(0xFF8E918F) else Color(0xFF747775)
    val text = when(signInMethod) {
        SignInMethod.GOOGLE -> "Continue with Google"

        SignInMethod.EMAIL -> "Continue with E-mail"
    }
    val contentDescription = when(signInMethod) {
        SignInMethod.GOOGLE -> "Google sign-in"

        SignInMethod.EMAIL -> "E-mail sign-in"
    }
    val painter = painterResource(when(signInMethod) {
        SignInMethod.GOOGLE -> R.drawable.ic_google_logo

        SignInMethod.EMAIL -> R.drawable.ic_email_logo
    })


    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor, contentColor = contentColor),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .height(40.dp)
            //.padding(horizontal = 12.dp, vertical = 10.dp)
            .fillMaxWidth(0.85f)

    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = text,
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(R.font.roboto_medium)) // Use the Roboto Medium font family here
            )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(12.dp))
        }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignInButton() {
   SignInButton(onClick = {}, isDarkTheme = false, signInMethod = SignInMethod.EMAIL)
}

@Preview(showBackground = true)
@Composable
fun PreviewGoogleSignInButtonDarkTheme() {
    SignInButton(onClick = {}, isDarkTheme = true, signInMethod = SignInMethod.EMAIL)
}

enum class SignInMethod(){
    GOOGLE, EMAIL
}