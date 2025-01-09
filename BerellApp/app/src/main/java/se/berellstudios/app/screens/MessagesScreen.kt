package se.berellstudios.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import se.berellstudios.app.RetrofitClient
import se.berellstudios.app.components.MenuAndImageBar
import se.berellstudios.app.components.MessageCreationDialog
import se.berellstudios.app.components.MessageList
import se.berellstudios.app.ui.theme.BerellAppTheme

//Controlling and showing the messages section of our code
@Composable
fun MessagesScreen(navController: NavController, mainViewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        mainViewModel.viewMessages()
    }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(8.dp)
                ) {
                    //Showing the top with logo, menu and text
                    MenuAndImageBar(navController, mainViewModel, "Messages")

                    if (RetrofitClient.getRole(context) == "admin") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Add new event")
                            SmallFloatingActionButton(
                                onClick = { showDialog = true },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    "Small floating action button for adding task."
                                )
                            }
                        }
                    }

                    //Displaying messages
                    MessageList(mainViewModel = mainViewModel)
                }
            }
        }
        if (showDialog) {
            MessageCreationDialog(
                onDismiss = { showDialog = false },
                onCreateMessage = { message ->
                    mainViewModel.createMessage(context, message)
                    showDialog = false
                },
                mainViewModel = mainViewModel
            )

        }
    }
}

