package se.berellstudios.app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.R

//Showing the top with logo, menu and text
@Composable
fun MenuAndImageBar(navController: NavController, mainViewModel: MainViewModel, textContent: String ) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween //Ensures space between elements
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier
                .width(40.dp)
                .padding(top = 16.dp)
                .clickable { navController.navigate("landing") }
                .semantics { contentDescription = "Syncd Logo" }
        )
        Text(
            textContent,
            style = MaterialTheme.typography.headlineMedium
        )
        Box(
            modifier = Modifier
                .semantics {
                    contentDescription = "Dropdown menu for alternatives in app"
                }
        ) {
            DropdownMenuWithDetails(
                navController,
                mainViewModel
            )
        }
    }
}