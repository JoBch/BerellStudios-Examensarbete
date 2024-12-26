package se.berellstudios.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import se.berellstudios.app.MainViewModel

@Composable
fun MessageList(viewModel: MainViewModel) {
    val messages by viewModel.messages.collectAsState()
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
            //Iterating through the messages from MVM and populating the LazyColumn 1 by 1
            items(messages) { message ->
                Text(
                    text = "" + message,
                    modifier = Modifier //TODO snygga till "CSS"
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray)
                        .padding(8.dp)
                )
            }
        }
    }
}
