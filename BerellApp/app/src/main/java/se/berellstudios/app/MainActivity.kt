package se.berellstudios.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import se.berellstudios.app.ui.theme.BerellAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = RetrofitClient.getToken(applicationContext)
        if (token != null) {
            // Token exists, navigate to start page
            setContent {
                BerellAppTheme {
                    AppNavigation(isLoggedIn = true) // Pass the logged-in state
                }
            }
        } else {
            // Token doesn't exist, navigate to login
            setContent {
                BerellAppTheme {
                    AppNavigation(isLoggedIn = false) // Pass the logged-in state
                }
            }
        }
    }
}


@Composable
fun AppNavigation(isLoggedIn: Boolean) {
    val navController = rememberNavController()

    // Start at "login" or "startpage" based on token presence
    val startDestination = if (isLoggedIn) "startpage" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            val mainViewModel: MainViewModel = viewModel()
            LogInScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
        composable("startpage") {
            StartPageScreen(navController = navController)
        }
        composable("createuser") {
            val mainViewModel: MainViewModel = viewModel()
            CreateUserScreen(
                navController = navController,
                mainViewModel = mainViewModel
                )
        }
    }
}


@Composable
fun StartPageScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Greeting(name = "JOEL ÄR INLOGGAD")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {

                            navController.navigate("login")
                        }
                    ) {
                        Text("Tillbaka till start")
                    }
                }
            }
        }
    }
}

@Composable
fun LogInScreen(navController: NavController, mainViewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    //Observe the login state
    val loggedIn by mainViewModel.loggedIn.observeAsState(false)

    //If the user is logged in, navigate to the start page
    if (loggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("startpage") {
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
                    Greeting(name = "Log In")
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
                            mainViewModel.login(context, username, password)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Log in")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //Ping Server button
                    Button(
                        onClick = {
                            APICalls.callPingApi(
                                context = context,
                                onSuccess = { message ->
                                    Toast.makeText(context, "Ping Success: $message", Toast.LENGTH_SHORT).show()
                                },
                                onError = { error ->
                                    Toast.makeText(context, "Ping Error: $error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ping Server")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //Create user button
                    Button(
                        onClick = {
                            navController.navigate("createuser")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create user")
                    }
                }
            }
        }
    }
}


@Composable
fun CreateUserScreen(navController: NavController, mainViewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
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
                    // Textfält för email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("your email please") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Textfält för användarnamn
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("feed me a GOOD username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Textfält för lösenord
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("a secure password PLEASE") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            //Calling register to register a user.
                            mainViewModel.register(email, username, password)

                            Log.i("Andreas", "CreateUserScreen: $username")
                            Log.i("Andreas", "CreateUserScreen: $email")
                            Log.i("Andreas", "CreateUserScreen: $password")

                            //This should be done in the register function
                            navController.navigate("login")
                        }
                    ) {
                        Text("Create user AKA gå tillbaka till logga in")
                    }
                }
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BerellAppTheme {
        Greeting("Android")
    }
}
