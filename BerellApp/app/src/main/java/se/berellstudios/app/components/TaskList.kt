package se.berellstudios.app.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel

@Composable
fun TaskList(
    mainViewModel: MainViewModel,
    status: String,
    isLandingScreen: Boolean,
    context: Context
) {
    val tasks by mainViewModel.tasks.collectAsState()
    val users = mainViewModel.users.collectAsState(initial = emptyList())

    // Filtrera tasks baserat på status
    val filteredTasks = tasks.filter { it.status == status }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(filteredTasks) { task ->
                TaskItem(
                    task = task,
                    context = context,
                    mainViewModel = mainViewModel,
                    isLandingScreen = isLandingScreen
                )
            }
        }
    }
}
