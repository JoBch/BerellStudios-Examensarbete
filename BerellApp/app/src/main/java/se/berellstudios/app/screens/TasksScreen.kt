package se.berellstudios.app.screens

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
import se.berellstudios.app.TaskDTO
import se.berellstudios.app.components.TaskList
import se.berellstudios.app.ui.theme.BerellAppTheme
import java.time.LocalDateTime

//Controlling and showing the tasks section of our code
@Composable
fun TasksScreen(navController: NavController, mainViewModel: MainViewModel) {
    var task_message by remember { mutableStateOf("") }
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
                    Text("Tasks")

                    OutlinedTextField(
                        value = task_message,
                        onValueChange = { task_message = it },
                        label = { Text("Enter task description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            //Create task
                            val task = TaskDTO(
                                id = null, //This one is set in the server
                                messageContent = task_message,
                                status = "todo", //Default värde, ska kanske använda en dropdown eller nåt här
                                deadline = LocalDateTime.now()
                                    .toString(), //TODO kolla på hur vi ska få rätt på denna
                                createdTime = "", //This one is set in the server
                                user_id = null //This one is set in the server
                            )
                            mainViewModel.createTask(context, task)
                        }
                    ) {
                        Text("Create Task")
                    }
                    Button(
                        onClick = {
                            //Navigate back to the landing screen
                            mainViewModel.viewTasks()
                        }
                    ) {
                        Text("Show me the tasks")
                    }
                    Button(
                        onClick = {
                            //Navigate back to the landing screen
                            navController.navigateUp()
                        }
                    ) {
                        Text("Back to Landing")
                    }

                    //Displaying tasks
                    TaskList(viewModel = mainViewModel)
                }
            }
        }
    }
}
