package se.berellstudios.app.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.MessageDTO
import se.berellstudios.app.TaskDTO

@Composable
fun MessageItem (
    message: MessageDTO,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    context: Context
) {
    val messages by mainViewModel.messages.collectAsState()
    val context = LocalContext.current
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
            Text(
                text = "" + message.message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.LightGray)
                    .padding(8.dp)
            )
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

            Text(
                text = "Deadline: ${message.deadline}",
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 4.dp)
            )
            androidx.compose.material3.IconButton(
                onClick = {
                    // HÄR UPPDATERAR MAN SIN TASK

                },
                modifier = Modifier
                    .padding(start = 8.dp) // Space mellan deadline-text och knappen
                    .semantics { contentDescription = "edit deadline button" }
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                    contentDescription = "Edit Icon",
                    tint = MaterialTheme.colorScheme.primary // Anpassa färgen på ikonen
                )
            }
        }
    }
}