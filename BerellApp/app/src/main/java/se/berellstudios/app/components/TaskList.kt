package se.berellstudios.app.components

import android.content.Context
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
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.TaskDTO

//Listan är överst
@Composable
fun TaskList(mainViewModel: MainViewModel, context: Context) {
    val tasks by mainViewModel.tasks.collectAsState()

    // Group tasks by their status
    val groupedTasks = tasks.groupBy { it.status }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tasks",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // LazyRow to display statuses in separate columns
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedTasks.keys.forEach { status ->
                item {
                    TaskColumn(
                        status = status,
                        tasks = groupedTasks[status] ?: emptyList(),
                        mainViewModel = mainViewModel,
                        context = context
                    )
                }
            }
        }
    }
}
