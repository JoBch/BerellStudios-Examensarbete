package se.berellstudios.app.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import se.berellstudios.app.APICalls
import se.berellstudios.app.components.Greeting
import se.berellstudios.app.ui.theme.BerellAppTheme
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.ui.theme.Purple40


//Landing page when loggedin=false
@Composable
fun LogInScreen(navController: NavController, viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    //Observe the login state
    val loggedIn by viewModel.loggedIn.observeAsState(false)

    //If the user is logged in, navigate to the start page
    if (loggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("landing") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Greeting(name = "New user")
                    Spacer(modifier = Modifier.height(16.dp))

                    //Username input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    //Show error message if any
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //Login button
                    Button(
                        onClick = {
                            //Call the login function from ViewModel
                            viewModel.login(context, username, password)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Log in")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //TODO ta bort denna när vi känner oss säkra på uppkopplingen mot server för att rensa kod
                    Button(
                        onClick = {
                            APICalls.callPingApi(
                                context = context,
                                onSuccess = { message ->
                                    Toast.makeText(
                                        context,
                                        "Ping Success: $message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = { error ->
                                    Toast.makeText(
                                        context,
                                        "Ping Error: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ping Server")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //Create user button
//                    Button(
//                        onClick = {
//                            navController.navigate("createuser")
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("Create user")
//                    }
                    Box(
                        modifier = Modifier.fillMaxSize() // Box fyller hela skärmen
                    ) {
                        // Andra UI-element här...

                        // Klickbar text som är placerad längst ner
                        Text(
                            text = "Click here to create a new account",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("createuser")
                                }
                                .align(Alignment.BottomCenter),  // Placerar texten längst ner
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = Purple40,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                }
            }
        }
    }
}
