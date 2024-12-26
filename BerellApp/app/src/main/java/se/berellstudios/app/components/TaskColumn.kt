package se.berellstudios.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import se.berellstudios.app.TaskDTO
import se.berellstudios.app.components.TaskItem

//Columnerna är mitten
@Composable
fun TaskColumn(status: String, tasks: List<TaskDTO>) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(150.dp)
            .padding(8.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercaseChar() },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            items(tasks) { task ->
                TaskItem(task)
            }
        }
    }
}
