package se.berellstudios.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import se.berellstudios.app.ui.theme.BerellAppTheme
import java.time.LocalDateTime

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


@Composable
//Navigating between composables
fun AppNavigation(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    //Start at "login" or "startpage" based on token presence
    val startDestination = if (isLoggedIn) "landing" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LogInScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }
        composable("landing") {
            LandingScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
        composable("messages") {
            MessagesScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
        composable("tasks") {
            TasksScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
        composable("createuser") {
            CreateUserScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }
    }
}

//TODO vi behöver olika landingScreen beroende på om det är admin eller user som loggar in.
//Vi kan få tag i det med getRole() nu
//Where we land if loggedin=true
@Composable
fun LandingScreen(navController: NavController, mainViewModel: MainViewModel) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Greeting(name = "JOEL ÄR INLOGGAD")
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate("messages")
                        }
                    ) {
                        Text("Go to Messages")
                    }

                    Button(
                        onClick = {
                            navController.navigate("tasks")
                        }
                    ) {
                        Text("Go to Tasks")
                    }

                    Button(
                        onClick = {
                            //Calling logout so we set loggedIn to false
                            mainViewModel.logout(context)
                            navController.navigate("login") //TODO: check how we can clean this up
                        }
                    ) {
                        Text("Logout")
                    }
                }
            }
        }
    }
}

//Controlling and showing the messages section of our code
@Composable
fun MessagesScreen(navController: NavController, mainViewModel: MainViewModel) {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text("Messages")

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Enter message to save to DB") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            //Calling createMessage
                            mainViewModel.createMessage(context, message)
                            Log.i("Andreas", "CreateMessage: $message")
                        }
                    ) {
                        Text("Create Message")
                    }
                    Button(
                        onClick = {
                            mainViewModel.viewMessages()
                        }
                    ) {
                        Text("Show me the Messages")
                    }
                    Button(
                        onClick = {
                            //Navigate back to the landing screen
                            navController.navigateUp()
                        }
                    ) {
                        Text("Back to Landing")
                    }

                    //Displaying messages
                    MessageList(viewModel = mainViewModel)
                }
            }
        }
    }
}

//Controlling and showing the tasks section of our code
@Composable
fun TasksScreen(navController: NavController, mainViewModel: MainViewModel) {
    var task_message by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text("Tasks")

                    OutlinedTextField(
                        value = task_message,
                        onValueChange = { task_message = it },
                        label = { Text("Enter task description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            //Create task
                            val task = TaskDTO(
                                id = null, //This one is set in the server
                                messageContent = task_message,
                                status = "todo", //Default värde, ska kanske använda en dropdown eller nåt här
                                deadline = LocalDateTime.now()
                                    .toString(), //TODO kolla på hur vi ska få rätt på denna
                                createdTime = "", //This one is set in the server
                                user_id = null //This one is set in the server
                            )
                            mainViewModel.createTask(context, task)
                        }
                    ) {
                        Text("Create Task")
                    }
                    Button(
                        onClick = {
                            //Navigate back to the landing screen
                            mainViewModel.viewTasks()
                        }
                    ) {
                        Text("Show me the tasks")
                    }
                    Button(
                        onClick = {
                            //Navigate back to the landing screen
                            navController.navigateUp()
                        }
                    ) {
                        Text("Back to Landing")
                    }

                    //Displaying tasks
                    TaskList(viewModel = mainViewModel)
                }
            }
        }
    }
}

//Landing page when loggedin=false
@Composable
fun LogInScreen(navController: NavController, viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    //Observe the login state
    val loggedIn by viewModel.loggedIn.observeAsState(false)

    //If the user is logged in, navigate to the start page
    if (loggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("landing") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Greeting(name = "Log In")
                    Spacer(modifier = Modifier.height(16.dp))

                    //Username input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    //Show error message if any
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //Login button
                    Button(
                        onClick = {
                            //Call the login function from ViewModel
                            viewModel.login(context, username, password)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Log in")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //TODO ta bort denna när vi känner oss säkra på uppkopplingen mot server för att rensa kod
                    Button(
                        onClick = {
                            APICalls.callPingApi(
                                context = context,
                                onSuccess = { message ->
                                    Toast.makeText(
                                        context,
                                        "Ping Success: $message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = { error ->
                                    Toast.makeText(
                                        context,
                                        "Ping Error: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ping Server")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //Create user button
                    Button(
                        onClick = {
                            navController.navigate("createuser")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create user")
                    }
                }
            }
        }
    }
}

//Creating user, navigate here from startpage
@Composable
fun CreateUserScreen(navController: NavController, viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BerellAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Greeting(name = "NY ANVÄNDARE SOM VILL SKAPA KONTO")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("your email please") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("feed me a GOOD username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("a secure password PLEASE") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            //Calling register to register a user.
                            viewModel.register(email, username, password)
                            navController.navigate("login")
                        }
                    ) {
                        Text("Create user AKA gå tillbaka till logga in")
                    }
                }
            }
        }
    }
}

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
