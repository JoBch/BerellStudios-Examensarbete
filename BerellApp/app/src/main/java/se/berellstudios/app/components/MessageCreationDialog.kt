package se.berellstudios.app.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.HapticFeedbackConstantsCompat
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.MessageDTO
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MessageCreationDialog(
    onDismiss: () -> Unit,
    onCreateMessage: (MessageDTO) -> Unit,
    mainViewModel: MainViewModel
) {
    var message by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var deadline by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val view = LocalView.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Create New Message",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Enter message to save to DB") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Show error message if any
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Button to pick a date and time
                Button(
                    onClick = {
                        showDateTimePicker(context) { dateTime ->
                            selectedDateTime = dateTime
                            deadline = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Pick a Date and Time")
                }

                // Show selected deadline
                if (deadline != null) {
                    Text(
                        text = "Selected Deadline: $deadline",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Create message button
                Button(
                    onClick = {
                        if (message.isBlank()) {
                            errorMessage = "Message is empty, please write SOMETHING"
                            view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                        } else if(deadline == null){
                            errorMessage = "Missing a deadline, please choose a deadline"
                            view.performHapticFeedback(HapticFeedbackConstantsCompat.KEYBOARD_PRESS)
                        }
                        else {
                            errorMessage = ""

                            val messageDTO = MessageDTO(
                                id = null, // Set by the server
                                message = message,
                                deadline = deadline,
                                createdAt = "", // Set by the server
                                user_id = null // Set by the server
                            )
                            onCreateMessage(messageDTO) // Notify parent about message creation
                            Log.i("Andreas", "CreateMessage: $message")
                            onDismiss() // Close dialog after successful creation
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Create Message")
                }

                // Cancel button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
