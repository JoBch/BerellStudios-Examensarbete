package se.berellstudios.app.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.HapticFeedbackConstantsCompat
import se.berellstudios.app.TaskDTO
import se.berellstudios.app.UserDTO
import se.berellstudios.app.screens.TaskStatus
import se.berellstudios.app.ui.theme.Pink80
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskEditDialog(
    taskDTO: TaskDTO,
    onDismiss: () -> Unit,
    onEditTask: (TaskDTO) -> Unit,
    users: List<UserDTO>
) { //Populating the values as deafault form the DTO
    var taskMessage by remember { mutableStateOf(taskDTO.messageContent) }
    var selectedStatus by remember { mutableStateOf(taskDTO.status) }
    var selectedPriority by remember { mutableStateOf(taskDTO.priority) }
    var selectedDateTime by remember { mutableStateOf(taskDTO.deadline) }
    var deadline by remember { mutableStateOf(taskDTO.deadline) }
    var selectedUser by remember { mutableStateOf(users.find { it.id == taskDTO.user_id }) }

    val context = LocalContext.current
    val view = LocalView.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edit Task ID: ${taskDTO.id}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Pink80)
                OutlinedTextField(
                    value = taskMessage,
                    onValueChange = { taskMessage = it },
                    label = { Text("Task Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "textfield, write new task here" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector(
                    label = "Status",
                    options = TaskStatus.entries.map { it.displayName },
                    selectedOption = selectedStatus,
                    onOptionSelected = { selectedStatus =
                        TaskStatus.entries.toTypedArray()[it].toString()
                    },
                    modifier = Modifier.semantics { contentDescription = "Dropdown to select current status of task" }
                )
                DropdownSelector(
                    label = "Priority",
                    options = listOf("1", "2", "3"),
                    selectedOption = selectedPriority.toString(),
                    onOptionSelected = { selectedPriority = it + 1 },
                    modifier = Modifier.semantics { contentDescription = "Dropdown to select priority of task" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector(
                    label = "Assigned User", //TODO kanske få till så att den visar username här istället
                    options = users.map { it.username },
                    selectedOption = selectedUser?.username ?: "None",
                    onOptionSelected = { index -> selectedUser = users[index] },
                    modifier = Modifier.semantics { contentDescription = "Dropdown to set a user to the task" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        showDateTimePicker(context) { dateTime ->
                            selectedDateTime = dateTime.toString()
                            deadline = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Button, set deadline for task" }
                ) {
                    Text("Pick a Date and Time")
                }
                Text("Selected Deadline: ${taskDTO.deadline ?: "None"}")
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedUser == null) {
                                Toast.makeText(context, "Please select a user", Toast.LENGTH_SHORT).show()
                                view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                            } else if (taskMessage.isEmpty()) {
                                Toast.makeText(context, "Please enter a task description", Toast.LENGTH_SHORT).show()
                                view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                            } else if (selectedDateTime == null) {
                                Toast.makeText(context, "Please select a deadline", Toast.LENGTH_SHORT).show()
                                view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                            }  else {
                                // Kopiera taskDTO och fortsätt
                                onEditTask(
                                    taskDTO.copy(
                                        messageContent = taskMessage,
                                        status = selectedStatus,
                                        deadline = deadline,
                                        priority = selectedPriority,
                                        createdTime = "",
                                        user_id = selectedUser!!.id
                                    )
                                )
                                onDismiss() // Stäng dialogen efter skapande
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .semantics { contentDescription = "button to edit task" }
                    ) {
                        Text("Edit task")
                    }

                }
            }
        }
    }
}
