package se.berellstudios.app.components

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.TaskDTO
import se.berellstudios.app.ui.theme.Pink40
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskItem(
    task: TaskDTO,
    modifier: Modifier = Modifier,
    context: Context,
    mainViewModel: MainViewModel,
    isLandingScreen: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Format för datum
    val DarkGreen = Color(0xFF006400)
    //Todays date in the same format as "deadline"
    val currentDate = LocalDateTime.now().toLocalDate()

    //Refractor deadline to localdate
    val deadlineDate = try {
        task.deadline?.let { deadline ->
            LocalDateTime.parse(deadline, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .toLocalDate()
        }
    } catch (e: Exception) {
        null
    }

    //Set colour based on deadline
    val deadlineColor = when {
        deadlineDate == null -> Color.Gray //If no deadline
        deadlineDate.isBefore(currentDate) -> Color.Red //If deadline passed
        else -> DarkGreen //If deadline not passed
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Column for the task to populate
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if (isLandingScreen) {
                        Text(
                            text = "Status: ${task.status}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Pink40
                            ),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .semantics { contentDescription = "task status ${task.status}" }
                        )
                    }
                    Text(
                        text = task.messageContent,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    deadlineDate?.let { date ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Deadline: ${date.format(dateFormatter)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = deadlineColor
                                ),
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .semantics { contentDescription = "deadline for the task" }
                            )
                            //Checking if the call comes from the landingscreen or not
                            if (!isLandingScreen) {
                                androidx.compose.material3.IconButton(
                                    onClick = {
                                        showDialog = true
                                    },
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .semantics { contentDescription = "edit deadline button" }
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                                        contentDescription = "Edit Icon",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .semantics { contentDescription = "Change task status" },
                                onClick = {
                                    mainViewModel.changeTaskStatus(context, task)
                                }

                            ) {
                                val buttonText = when (task.status) {
                                    "todo" -> "DOING IT"
                                    "ongoing" -> "DONE"
                                    "done" -> "DELETE"
                                    else -> "UNKNOWN"
                                }
                                Text(buttonText)
                            }
                        }
                    }


                }
            }
        }
        if (showDialog) {
            TaskEditDialog(
                taskDTO = task,
                onDismiss = { showDialog = false },
                onEditTask = { editedTask ->
                    mainViewModel.editTask(context, editedTask) //Update task
                    showDialog = false
                },
                users = mainViewModel.users.collectAsState().value
            )
        }
    }
}
