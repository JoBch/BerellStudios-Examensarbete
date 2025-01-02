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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import se.berellstudios.app.TaskDTO
import se.berellstudios.app.UserDTO
import se.berellstudios.app.screens.TaskStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun TaskCreationDialog(
    onDismiss: () -> Unit,
    onCreateTask: (TaskDTO) -> Unit,
    users: List<UserDTO>
) {
    var taskMessage by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(TaskStatus.TODO) }
    var selectedPriority by remember { mutableStateOf(3) }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var deadline by remember { mutableStateOf<String?>(null) }
    var selectedUser by remember { mutableStateOf<UserDTO?>(null) }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Create New Task", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(
                    value = taskMessage,
                    onValueChange = { taskMessage = it },
                    label = { Text("Task Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector(
                    label = "Status",
                    options = TaskStatus.entries.map { it.displayName },
                    selectedOption = selectedStatus.displayName,
                    onOptionSelected = { selectedStatus = TaskStatus.values()[it] }
                )
                DropdownSelector(
                    label = "Priority",
                    options = listOf("1", "2", "3"),
                    selectedOption = selectedPriority.toString(),
                    onOptionSelected = { selectedPriority = it + 1 }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector(
                    label = "Assign to User",
                    options = users.map { it.username },
                    selectedOption = selectedUser?.username ?: "None",
                    onOptionSelected = { index -> selectedUser = users[index] }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        showDateTimePicker(context) { dateTime ->
                            selectedDateTime = dateTime
                            deadline = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick a Date and Time")
                }
                Text("Selected Deadline: ${deadline ?: "None"}")
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedUser != null) {
                                onCreateTask(
                                    TaskDTO(
                                        id = null,
                                        messageContent = taskMessage,
                                        status = selectedStatus.dbValue,
                                        deadline = deadline,
                                        priority = selectedPriority,
                                        createdTime = "",
                                        user_id = selectedUser!!.id
                                    )
                                )
                                onDismiss()
                            } else {
                                Toast.makeText(context, "Please select a user", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}
