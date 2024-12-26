package se.berellstudios.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.components.TaskColumn

//Listan är överst
@Composable
fun TaskList(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    //Group tasks by their status
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

        //LazyRow to display the statuses in separate columns
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Iterate over the statuses and create a column for each status
            items(groupedTasks.keys.toList()) { status ->
                if (groupedTasks[status].isNullOrEmpty()) {
                    Text(text = "No tasks for $status")
                } else {
                    TaskColumn(
                        status = status,
                        tasks = groupedTasks[status] ?: emptyList()
                    )
                }
            }
        }
    }
}
