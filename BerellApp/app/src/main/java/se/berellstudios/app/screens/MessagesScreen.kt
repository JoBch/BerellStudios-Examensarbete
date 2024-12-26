package se.berellstudios.app.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.components.MessageList
import se.berellstudios.app.ui.theme.BerellAppTheme

//Controlling and showing the messages section of our code
@Composable
fun MessagesScreen(navController: NavController, mainViewModel: MainViewModel) {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text("Messages")

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Enter message to save to DB") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            //Calling createMessage
                            mainViewModel.createMessage(context, message)
                            Log.i("Andreas", "CreateMessage: $message")
                        }
                    ) {
                        Text("Create Message")
                    }
                    Button(
                        onClick = {
                            mainViewModel.viewMessages()
                        }
                    ) {
                        Text("Show me the Messages")
                    }
                    Button(
                        onClick = {
                            //Navigate back to the landing screen
                            navController.navigateUp()
                        }
                    ) {
                        Text("Back to Landing")
                    }

                    //Displaying messages
                    MessageList(viewModel = mainViewModel)
                }
            }
        }
    }
}
