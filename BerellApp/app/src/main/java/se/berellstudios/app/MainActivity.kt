package se.berellstudios.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.berellstudios.app.ui.theme.BerellAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BerellAppTheme {
                AppNavigation()
            }
        }
    }


    fun callPingApi() {
        RetrofitClient.apiService.ping().enqueue(object : Callback<PingResponse> {
            override fun onResponse(call: Call<PingResponse>, response: Response<PingResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "No message"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Server Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PingResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.d("Berell", "\"Network Error: ${t.message}\"")
            }
        })
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current as MainActivity

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LogInScreen(
                navController = navController,
                callPingApi = { context.callPingApi() }
            )
        }
        composable("startpage") { StartPageScreen(navController) }
        composable("createuser") { CreateUserScreen(navController) }
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
fun LogInScreen(navController: NavController, callPingApi: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Greeting(name = "User")
                    Spacer(modifier = Modifier.height(16.dp))

                    // Textfält för användarnamn
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Textfält för lösenord
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ping Server-knappen
                    Button(
                        onClick = {
                            Log.i("Andreas", "LogInUserScreen: $username")
                            Log.i("Andreas", "LogInUserScreen: $password")

                            //kod för att spara något i sharePref?
                            navController.navigate("startpage")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("logga in")
                    }
                    Button(
                        onClick = {
                            callPingApi() // Anropa callPingApi här
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ping Server")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Skapa användare-knappen
                    Button(
                        onClick = {

                            navController.navigate("createuser")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Skapa användare")
                    }
                }
            }
        }
    }
}

@Composable
fun CreateUserScreen(navController: NavController) {
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
                    // Textfält för användarnamn
                    // Textfält för användarnamn
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
                    // Textfält för användarnamn
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("a secure password PLEASE") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            Log.i("Andreas", "CreateUserScreen: $username")
                            Log.i("Andreas", "CreateUserScreen: $email")
                            Log.i("Andreas", "CreateUserScreen: $password")
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







/*
 * Funktion för att spara token till SharedPreferences

private fun saveToken(context: Context, token: String) {
    val PREFS_NAME = "MyAppPrefs"
    val TOKEN_KEY = "jwt_token"
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
} */
