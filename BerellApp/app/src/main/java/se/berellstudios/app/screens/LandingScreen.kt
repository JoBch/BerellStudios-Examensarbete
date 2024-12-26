package se.berellstudios.app.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import se.berellstudios.app.ui.theme.BerellAppTheme
import se.berellstudios.app.MainViewModel


//TODO vi behöver olika landingScreen beroende på om det är admin eller user som loggar in.
//Vi kan få tag i det med getRole() nu
//Where we land if loggedin=true

@Composable
fun LandingScreen(navController: NavController, mainViewModel: MainViewModel) {
    val context: Context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    //HÄR VILL JAG FÅ IN NAMN och ROLE PÅ DEN INLOGGADE
                    Text("Welcome, you're logged in!")
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { navController.navigate("messages") }) {
                        Text("Go to Messages")
                    }

                    Button(onClick = { navController.navigate("tasks") }) {
                        Text("Go to Tasks")
                    }

                    Button(onClick = {
                        mainViewModel.logout(context)
                        navController.navigate("login")
                    }) {
                        Text("Logout")
                    }
                }
            }
        }
    }
}
