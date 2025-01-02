package se.berellstudios.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel

//Listan är överst
@Composable
fun TaskList(mainViewModel: MainViewModel, status: String) {
    val tasks by mainViewModel.tasks.collectAsState()

    // Filter tasks based on the provided status
    val filteredTasks = tasks.filter { it.status == status }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tasks",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // LazyRow to display tasks for the current status
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TaskColumn(
                    status = status,
                    tasks = filteredTasks,
                    mainViewModel = mainViewModel,
                    context = LocalContext.current // Pass current context
                )
            }
        }
    }
}
