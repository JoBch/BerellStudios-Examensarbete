package se.berellstudios.app.components

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.TaskDTO
import se.berellstudios.app.ui.theme.Pink80
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//val gradientColors = listOf(Cyan, PurpleGrey40, Pink40 /*...*/)
/*style = TextStyle(
            brush = Brush.linearGradient(
                colors = gradientColors
            )
        ),*/

//Itemsen är lägst
@Composable
fun TaskItem(
    task: TaskDTO,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    context: Context
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Format för datum

    val deadlineDate = try {
        task.deadline?.let { deadline ->
            // Omvandla deadline från String till LocalDateTime
            LocalDateTime.parse(deadline.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .toLocalDate()
        }
    } catch (e: Exception) {
        null // Hantera eventuell parse-error
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            //.padding(8.dp)
    ) {
        // Bakgrund med border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(35.dp) // Padding inuti boxen för innehåll
        ) {
            // Använd Column för att placera innehållet vertikalt
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Taskens innehåll
                Text(
                    text = task.messageContent,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(15.dp))
                // Visa deadline
                deadlineDate?.let { date ->
                    Text(
                        text = "Deadline: ${date.format(dateFormatter)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        ),
                        modifier =
                        Modifier.padding(top = 8.dp, bottom = 8.dp)

                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

            }
        }

        // Statusetikett placerad "i ramen"
        Text(
            text = task.status,
            style = MaterialTheme.typography.bodySmall,
            color = Pink80,
            modifier = Modifier
                .padding(horizontal = 4.dp) // Litet utrymme runt texten
                .align(Alignment.TopStart) // Justering till toppen av boxen
                .offset(y = (-10).dp) // Flytta etiketten uppåt för att ligga "i ramen"
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Knapp som ändrar status på tasken
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Placera knappen längst ner i boxen
                .padding(bottom = 8.dp, top = 8.dp),

            onClick = {
                mainViewModel.changeTaskStatus(context, task)
            }
        ) {
            val buttonText = when (task.status) {
                "todo" -> "DOING IT"
                "ongoing" -> "DONE"
                "done" -> "ARCHIVE"
                else -> "UNKNOWN"
            }
            Text(buttonText)
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Checkmark om status är "done"
        if (task.status == "done") {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Check mark",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Placera ikonen längst ner till höger
                    .padding(8.dp)
            )
        }
    }
}