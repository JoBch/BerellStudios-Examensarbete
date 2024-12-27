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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import se.berellstudios.app.components.Greeting
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.ui.theme.BerellAppTheme

//Creating user, navigate here from startpage
@Composable
fun CreateUserScreen(navController: NavController, viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Greeting(name = "NY ANVÄNDARE SOM VILL SKAPA KONTO")
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

                    // Display error message if any
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red, // You can change the color to something else if preferred
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Button(
                        onClick = {
                            if (email.isBlank() || username.isBlank() || password.isBlank()){
                                errorMessage = "Something is missing, check all fields again"
                            }  else if (!isValidEmail(email)) {
                                errorMessage = "Invalid email address"
                            } else{
                                errorMessage = ""
                                //Calling register to register a user.
                                viewModel.register(email, username, password)
                                navController.navigate("login")
                            }

                        }
                    ) {
                        Text("Create user AKA gå tillbaka till logga in")
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