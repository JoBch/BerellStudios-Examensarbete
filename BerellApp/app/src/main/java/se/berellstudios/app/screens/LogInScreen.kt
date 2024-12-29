package se.berellstudios.app.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.components.Greeting
import se.berellstudios.app.ui.theme.BerellAppTheme
import se.berellstudios.app.ui.theme.Purple40


//Landing page when loggedin=false
@Composable
fun LogInScreen(navController: NavController, mainViewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    val context = LocalContext.current

    //Observe the login state
    val loggedIn by mainViewModel.loggedIn.observeAsState(false)

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

                    //Building this in a row to make it align properly
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remember Me", style = TextStyle(fontSize = 16.sp))
                    }

                    //Login button
                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                errorMessage = "Username and password are required"
                            } else {
                                //Nollställ felmeddelandet om båda fälten är ifyllda
                                errorMessage = ""
                                //Anropa login-funktionen och hantera eventuella fel
                                mainViewModel.login(
                                    context,
                                    username,
                                    password,
                                    rememberMe
                                ) { loginResult ->
                                    if (loginResult == "Invalid credentials") {
                                        errorMessage = "Invalid username or password"
                                    } else if (loginResult == "Error") {
                                        errorMessage = "An error occurred, please try again"
                                    }
                                }
                            }
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

                    Box(
                        modifier = Modifier.fillMaxSize() //Box fyller hela skärmen
                    ) {

                        //Klickbar text som är placerad längst ner
                        Text(
                            text = "Click here to create a new account",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("createuser")
                                }
                                .align(Alignment.BottomCenter),  //Placerar texten längst ner
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
