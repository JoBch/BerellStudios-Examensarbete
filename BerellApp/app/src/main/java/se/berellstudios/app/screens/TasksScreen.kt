package se.berellstudios.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    var isLandingScreen by remember { mutableStateOf(false) }

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
                        .padding(16.dp)
                ) {
                    Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tasks", style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .semantics { contentDescription = "Dropdown menu for alternatives in app" }

                    ) {

                        DropdownMenuWithDetails(navController, mainViewModel) // Menyn hamnar till höger
                    }
                }
                    // Header och skapa ny task-knapp

                    if (RetrofitClient.getRole(context) == "admin") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Add new task")
                            SmallFloatingActionButton(
                                onClick = { showDialog = true  },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(Icons.Filled.Add, "Small floating action button for adding task.")
                            }
                        }
                    }
                    // Tabbar för Task Status
                    val tabs = TaskStatus.entries
                    TabRow(selectedTabIndex = tabs.indexOf(selectedTab),
                        modifier = Modifier.semantics { contentDescription = "Tabs for different task statuses" }) {
                        tabs.forEach { tab ->
                            Tab(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                text = { Text(tab.displayName) },
                                modifier = Modifier.semantics {
                                    contentDescription = "Tab for ${tab.displayName}"
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Visa uppgifter för vald status
                    when (selectedTab) {
                        TaskStatus.TODO -> TaskList(mainViewModel = mainViewModel, status = TaskStatus.TODO.dbValue, isLandingScreen)
                        TaskStatus.ONGOING -> TaskList(mainViewModel = mainViewModel, status = TaskStatus.ONGOING.dbValue, isLandingScreen)
                        TaskStatus.DONE -> TaskList(mainViewModel = mainViewModel, status = TaskStatus.DONE.dbValue, isLandingScreen)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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

