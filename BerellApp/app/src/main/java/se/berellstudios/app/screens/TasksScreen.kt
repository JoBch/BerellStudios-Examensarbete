package se.berellstudios.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import se.berellstudios.app.TaskDTO
import se.berellstudios.app.components.TaskList
import se.berellstudios.app.components.showDateTimePicker
import se.berellstudios.app.ui.theme.BerellAppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class TaskStatus(val dbValue: String, val displayName: String) {
    TODO("todo", "ToDo"),
    ONGOING("ongoing", "Ongoing"),
    DONE("done", "Done")
}

@Composable
fun TasksScreen(navController: NavController, mainViewModel: MainViewModel) {
    mainViewModel.viewTasks()
    val priority = intArrayOf(1, 2, 3)
    val context = LocalContext.current
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var deadline by remember { mutableStateOf<String?>(null) }
    var taskMessage by remember { mutableStateOf("") }
    var statusExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(TaskStatus.TODO) }
    var selectedPriority by remember { mutableIntStateOf(3) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text("Tasks", style = MaterialTheme.typography.headlineMedium)

                    if (RetrofitClient.getRole(context) == "admin") {
                        OutlinedTextField(
                            value = taskMessage,
                            onValueChange = { taskMessage = it },
                            label = { Text("Enter task description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { statusExpanded = !statusExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Status: ${selectedStatus.displayName}")
                        }
                        //TODO se till så att denna hamnar där vi vill
                        DropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            TaskStatus.entries.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.displayName) },
                                    onClick = {
                                        selectedStatus = status
                                        statusExpanded = false
                                    },
                                    leadingIcon = {
                                        if (status == selectedStatus) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                            }
                        }
                        Button(
                            onClick = { priorityExpanded = !priorityExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Prio: $selectedPriority")
                        }
                        DropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false }
                        ) {
                            priority.forEach { prio ->
                                DropdownMenuItem(
                                    text = { Text("" + prio) },
                                    onClick = {
                                        selectedPriority = prio
                                        priorityExpanded = false
                                    },
                                    leadingIcon = {
                                        if (prio == selectedPriority) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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


                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                //Create task
                                val task = TaskDTO(
                                    id = null, //Set by the server
                                    messageContent = taskMessage,
                                    status = selectedStatus.dbValue,
                                    deadline = deadline,
                                    priority = selectedPriority,
                                    createdTime = "", //Set by the server
                                    user_id = null //Set by the server
                                )
                                mainViewModel.createTask(context, task)
                                //TODO denna verkar ej funka här, hinner kanske inte med
                                mainViewModel.viewTasks()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create Task")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Landing")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TaskList(viewModel = mainViewModel)
                }
            }
        }
    }
}