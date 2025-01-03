package se.berellstudios.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.navigation.NavController
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.components.Greeting
import se.berellstudios.app.ui.theme.BerellAppTheme

@Composable
fun CreateUserScreen(navController: NavController, mainViewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Greeting(name = "new User who wants to create account")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("your email please") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("feed me a GOOD username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("a secure password PLEASE") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp),
                            style = TextStyle(fontSize = 14.sp) // För flexibilitet med skärmar
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Button(
                            onClick = {
                                if (email.isBlank() || username.isBlank() || password.isBlank()) {
                                    errorMessage = "Something is missing, check all fields again"
                                    view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                                } else if (!isValidEmail(email)) {
                                    errorMessage = "Invalid email address"
                                    view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                                } else {
                                    errorMessage = ""
                                    mainViewModel.register(email, username, password)
                                    navController.navigate("login")
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter) // Placera knappen längst ner
                                .padding(bottom = 16.dp) // För estetik
                        ) {
                            Text(
                                "Create user and go back to log in",
                                style = TextStyle(fontSize = 16.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
    return email.matches(emailRegex)
}
