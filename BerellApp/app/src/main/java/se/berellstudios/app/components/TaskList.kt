package se.berellstudios.app.components

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel

//Listan är överst
@Composable
fun TaskList(mainViewModel: MainViewModel, status: String, isLandingScreen: Boolean) {
    val tasks by mainViewModel.tasks.collectAsState()


    // Filtrera tasks baserat på status
    val filteredTasks = tasks.filter { it.status == status }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        /*Text(
            text = status.replaceFirstChar { it.uppercaseChar() },
            modifier = Modifier.padding(bottom = 8.dp)
        )*/

        // Använd LazyColumn direkt för att visa tasks
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(filteredTasks) { task ->
                TaskItem(
                    task = task,
                    mainViewModel = mainViewModel,
                    context = LocalContext.current,
                    isLandingScreen = isLandingScreen
                )
            }
        }
    }
}
