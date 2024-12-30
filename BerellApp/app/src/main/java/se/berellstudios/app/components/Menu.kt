package se.berellstudios.app.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.rpc.Help
import se.berellstudios.app.MainViewModel

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

}

@Composable
fun DropdownMenuWithDetails(navController: NavController, mainViewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    mainViewModel.viewStarterTasks()
    val context: Context = LocalContext.current

    Box {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // First section
            DropdownMenuItem(
                text = { Text("Profile") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                onClick = { showToast(context, "PROFILE UNDER CONSTRUCTION") }
            )
            DropdownMenuItem(
                text = { Text("Settings") },
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                onClick = { showToast(context, "SETTINGS UNDER CONSTRUCTION") }
            )

            HorizontalDivider()

            // Second section
            DropdownMenuItem(
                text = { Text("Home") },
                leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                onClick = { navController.navigate("landing") }
            )

            DropdownMenuItem(
                text = { Text("Tasks") },
                leadingIcon = { Icon(Icons.Outlined.Check, contentDescription = null) },
                onClick = { navController.navigate("tasks") }
            )
            DropdownMenuItem(
                text = { Text("Messages OR Events") },
                leadingIcon = { Icon(Icons.Outlined.DateRange, contentDescription = null) },
                onClick = { navController.navigate("messages") }
            )

            HorizontalDivider()

            // Third section
            DropdownMenuItem(
                text = { Text("About") },
                leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                onClick = { showToast(context, "HEJ JOEL OCH ANDREAS HAR GJORT DENNA APPEN") }
            )
            DropdownMenuItem(
                text = { Text("Log out") },
                leadingIcon = { Icon(Icons.Outlined.Warning, contentDescription = null) },
                onClick = {
                    mainViewModel.logout(context)
                    navController.navigate("login")
                }
            )
        }
    }
}
