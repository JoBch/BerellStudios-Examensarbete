package se.berellstudios.app.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import se.berellstudios.app.MessageDTO
import se.berellstudios.app.RetrofitClient
import se.berellstudios.app.components.MessageList
import se.berellstudios.app.components.showDateTimePicker
import se.berellstudios.app.ui.theme.BerellAppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//Controlling and showing the messages section of our code
@Composable
fun MessagesScreen(navController: NavController, mainViewModel: MainViewModel) {
    mainViewModel.viewMessages()
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var deadline by remember { mutableStateOf<String?>(null) }
    var messageInput by remember { mutableStateOf("") }
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
                    if (RetrofitClient.getRole(context) == "admin") {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            label = { Text("Enter message to save to DB") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                showDateTimePicker(context) { dateTime ->
                                    selectedDateTime = dateTime
                                    deadline =
                                        dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pick a Date and Time")
                        }

                        Text(
                            text = "Selected Deadline: ${deadline ?: "None"}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Button(
                            onClick = {
                                val message = MessageDTO(
                                    id = null, //Set by the server
                                    message = messageInput,
                                    createdBy = null, //Set by the server
                                    createdTime = "", //Set by the server
                                    deadline = deadline
                                )
                                //Calling createMessage
                                mainViewModel.createMessage(context, message)
                                //TODO denna verkar ej funka här, hinner kanske inte med
                                mainViewModel.viewMessages()
                                Log.i("Andreas", "CreateMessage: $message")
                            }
                        ) {
                            Text("Create Message")
                        }
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
