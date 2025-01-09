package se.berellstudios.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel

@Composable
fun MessageList(mainViewModel: MainViewModel) {
    val messages by mainViewModel.messages.collectAsState()
    val context = LocalContext.current
    //Building the messagelist using the stateflow populated in MVM
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Messages",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn {
            items(messages) { message ->
                MessageItem(
                    message = message,
                    mainViewModel = mainViewModel,
                    context = LocalContext.current,
                )
            }
        }
    }
}

