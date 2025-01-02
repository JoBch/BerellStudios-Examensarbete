package se.berellstudios.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import se.berellstudios.app.components.DropdownMenuWithDetails
import se.berellstudios.app.components.TaskCreationDialog
import se.berellstudios.app.components.TaskList
import se.berellstudios.app.ui.theme.BerellAppTheme

enum class TaskStatus(val dbValue: String, val displayName: String) {
    TODO("todo", "ToDo"),
    ONGOING("ongoing", "Ongoing"),
    DONE("done", "Done")
}

@Composable
fun TasksScreen(navController: NavController, mainViewModel: MainViewModel) {
    mainViewModel.viewTasks()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(TaskStatus.ONGOING) }
    val users by mainViewModel.users.collectAsState()

    // Ladda användare när skärmen laddas
    LaunchedEffect(Unit) {
        mainViewModel.getAllUsers()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                        Text("Tasks", style = MaterialTheme.typography.headlineMedium)

                    Box(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        DropdownMenuWithDetails(navController, mainViewModel) // Menyn hamnar till höger
                    }
                }
                    // Header och skapa ny task-knapp

                    if (RetrofitClient.getRole(context) == "admin") {
                        Button(onClick = { showDialog = true }) {
                            Text("Create new task")
                        }
                    }

                    // Tabbar för Task Status
                    val tabs = TaskStatus.entries
                    TabRow(selectedTabIndex = tabs.indexOf(selectedTab)) {
                        tabs.forEach { tab ->
                            Tab(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                text = { Text(tab.displayName) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Visa uppgifter för vald status
                    when (selectedTab) {
                        TaskStatus.TODO -> TaskList(mainViewModel = mainViewModel, status = TaskStatus.TODO.dbValue)
                        TaskStatus.ONGOING -> TaskList(mainViewModel = mainViewModel, status = TaskStatus.ONGOING.dbValue)
                        TaskStatus.DONE -> TaskList(mainViewModel = mainViewModel, status = TaskStatus.DONE.dbValue)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tillbaka till Landing
                    Button(onClick = { navController.navigateUp() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Back to Landing")
                    }
                }

                // Dialog för att skapa en uppgift
                if (showDialog) {
                    TaskCreationDialog(
                        onDismiss = { showDialog = false },
                        onCreateTask = { task ->
                            mainViewModel.createTask(context, task)
                            showDialog = false
                        },
                        users = users // Skickar användarlistan till dialogen
                    )
                }
            }
        }
    }
}
