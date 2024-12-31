package se.berellstudios.app.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.MessageDTO
import se.berellstudios.app.RetrofitClient
import se.berellstudios.app.components.DropdownMenuWithDetails
import se.berellstudios.app.components.MessageList
import se.berellstudios.app.components.showDateTimePicker
import se.berellstudios.app.ui.theme.BerellAppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//Controlling and showing the messages section of our code
@Composable
fun MessagesScreen(navController: NavController, mainViewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        mainViewModel.viewMessages()
    }
    var message by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var deadline by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) { Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Messages",
                        modifier = Modifier.weight(1f) // Texten fyller utrymmet horisontellt
                    )
                    Box(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        DropdownMenuWithDetails(navController, mainViewModel) // Menyn hamnar till höger
                    }
                }

                    if (RetrofitClient.getRole(context) == "admin") {
                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Enter message to save to DB") },
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

                        Button(
                            onClick = {
                                if (message.isBlank()) {
                                    errorMessage = "message is empty, please write SOMETHING"
                                } else {
                                    errorMessage = ""

                                    val messageDTO = MessageDTO(
                                        id = null, //Set by the server
                                        message = message,
                                        deadline = deadline,
                                        createdTime = "", //Set by the server
                                        createdBy = null //Set by the server
                                    )
                                    //Calling createMessage
                                    mainViewModel.createMessage(context, messageDTO)
                                    Log.i("Andreas", "CreateMessage: $message")
                                }
                            }
                        ) {
                            Text("Create Message")
                        }
                    }

                    //Displaying messages
                    MessageList(viewModel = mainViewModel)
                }
            }
        }
    }
}
