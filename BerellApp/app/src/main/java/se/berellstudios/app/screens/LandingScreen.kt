package se.berellstudios.app.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.R
import se.berellstudios.app.RetrofitClient
import se.berellstudios.app.components.DropdownMenuWithDetails
import se.berellstudios.app.components.TaskItem
import se.berellstudios.app.ui.theme.BerellAppTheme

//where we land if logIn=true
@Composable
fun LandingScreen(navController: NavController, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val tasks by mainViewModel.tasks.collectAsState()
    var isLandingScreen by remember { mutableStateOf(true) }

    BackHandler {
    }
    LaunchedEffect(Unit) {
        mainViewModel.viewStarterTasks()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "logo",
                            modifier = Modifier
                                .width(40.dp)
                                .padding(top = 16.dp)
                                .semantics { contentDescription = "Syncd Logo" }
                        )
                        Text(
                            text = "Welcome, you're logged in! Role: ${
                                RetrofitClient.getRole(
                                    context
                                )
                            }",
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .semantics {
                                    contentDescription = "Dropdown menu for alternatives in app"
                                }
                        ) {
                            DropdownMenuWithDetails(
                                navController,
                                mainViewModel
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Visa de tre uppgifter med närmast deadline
                    Text("Tasks with the nearest deadline:")
                    if (tasks.isEmpty()) {
                        Text("No tasks available.")
                    } else {
                        tasks.take(3).forEach { task ->
                            TaskItem(
                                task = task,
                                mainViewModel = mainViewModel, // Skicka med mainViewModel
                                context = context, // Skicka med context
                                isLandingScreen = isLandingScreen,
                            )
                        }
                    }
                    Text(text = "See more TASKS here",
                        modifier = Modifier
                            .clickable {
                                navController.navigate("tasks")
                            }
                            .semantics {
                                contentDescription = "Clickable text to get to Task page"
                            })
                }
            }
        }
    }
}
