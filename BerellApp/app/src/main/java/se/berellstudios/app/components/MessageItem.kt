package se.berellstudios.app.components

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.MessageDTO

@Composable
fun MessageItem(
    message: MessageDTO,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    context: Context
) {
    var showDialog by remember { mutableStateOf(false) }
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
                .padding(16.dp) // Padding inuti boxen
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Event: " + message.message,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = "Date: ${message.deadline}",
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically // Vertikal centrering av text och knapp
                ) {
                    IconButton(
                        onClick = {
                            mainViewModel.deleteMessage(context, message)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete message",
                            tint = Color.Red
                        )
                    }
                    androidx.compose.material3.IconButton(
                        onClick = {
                            showDialog = true

                        },
                        modifier = Modifier
                            .padding(start = 8.dp) // Space mellan deadline-text och knappen
                            .semantics { contentDescription = "edit deadline button" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Icon",
                            tint = MaterialTheme.colorScheme.primary // Anpassa färgen på ikonen
                        )
                    }
                }


            }
            if (showDialog) {
                MessageEditDialog(
                    messageDTO = message,
                    onDismiss = { showDialog = false },
                    onEditMessage = { editedMessage ->
                        mainViewModel.editMessage(context, editedMessage) //Update message
                        showDialog = false
                    },
                )
            }
        }
    }
}