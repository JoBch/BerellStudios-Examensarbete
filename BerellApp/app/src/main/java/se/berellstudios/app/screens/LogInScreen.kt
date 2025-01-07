package se.berellstudios.app.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.navigation.NavController
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.R
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
    val view = LocalView.current

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

    BackHandler {
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
                        modifier = Modifier
                            .fillMaxWidth(),


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
                            onCheckedChange = { rememberMe = it },
                            modifier = Modifier.semantics {
                                contentDescription = "Remember me checkbox"
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Remember Me",
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier
                                .clickable { rememberMe = !rememberMe }
                                .semantics { contentDescription = "Tap to toggle remember me" }
                        )
                    }

                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                errorMessage = "Username and password are required"
                                view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                            } else {
                                errorMessage = ""
                                mainViewModel.login(
                                    context,
                                    username,
                                    password,
                                    rememberMe
                                ) { loginResult ->
                                    if (loginResult == "Invalid credentials") {
                                        errorMessage = "Invalid username or password"
                                        view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                                    } else if (loginResult == "Error") {
                                        errorMessage = "An error occurred, please try again"
                                        view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Log in button" }
                    ) {
                        Text("Log in")
                    }

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .semantics { contentDescription = "Syncd Logo" }
                    )
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "Click here to create a new account",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("createuser")
                                    view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                                }
                                .align(Alignment.BottomCenter) // Placera längst ner i Boxen
                                .padding(bottom = 16.dp), // Lägg till lite padding för bättre utseende
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