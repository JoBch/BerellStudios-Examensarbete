package se.berellstudios.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import se.berellstudios.app.ui.theme.BerellAppTheme
import se.berellstudios.app.Navigation.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.loadToken(applicationContext)
        val token = RetrofitClient.getToken(applicationContext)
        if (token != null) {
            //Token exists, navigate to start page
            setContent {
                BerellAppTheme {
                    AppNavigation(isLoggedIn = true)
                }
            }
        } else {
            //Token doesnt exist, navigate to login
            setContent {
                BerellAppTheme {
                    AppNavigation(isLoggedIn = false)
                }
            }
        }
    }
}

//TODO vi behöver olika landingScreen beroende på om det är admin eller user som loggar in.
//Vi kan få tag i det med getRole() nu
//Where we land if loggedin=true

//Controlling and showing the messages section of our code



//Controlling and showing the tasks section of our code

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

//Listan är överst
@Composable
fun TaskList(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    //Group tasks by their status
    val groupedTasks = tasks.groupBy { it.status }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tasks",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        //LazyRow to display the statuses in separate columns
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Iterate over the statuses and create a column for each status
            items(groupedTasks.keys.toList()) { status ->
                if (groupedTasks[status].isNullOrEmpty()) {
                    Text(text = "No tasks for $status")
                } else {
                    TaskColumn(
                        status = status,
                        tasks = groupedTasks[status] ?: emptyList()
                    )
                }
            }
        }
    }
}

//Columnerna är mitten
@Composable
fun TaskColumn(status: String, tasks: List<TaskDTO>) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(150.dp)
            .padding(8.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercaseChar() },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            items(tasks) { task ->
                TaskItem(task)
            }
        }
    }
}

//Itemsen är lägst
@Composable
fun TaskItem(task: TaskDTO) {
    Text(
        text = task.messageContent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray)
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
    )
}


//TODO hejdå till detta?
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BerellAppTheme {
        Greeting("Android")
    }
}
