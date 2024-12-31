package se.berellstudios.app

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.contracts.Effect

class MainViewModel : ViewModel() {

    private val _loggedIn = MutableLiveData(false)
    val loggedIn: LiveData<Boolean> get() = _loggedIn

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    private val _tasks = MutableStateFlow<List<TaskDTO>>(emptyList())
    val tasks: StateFlow<List<TaskDTO>> = _tasks

    private val _users = MutableStateFlow<List<UserDTO>>(emptyList())
    val users: StateFlow<List<UserDTO>> = _users

    fun login(
        context: Context,
        email: String,
        password: String,
        rememberMe: Boolean,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val loginRequest = UserLoginRequest(email, password, rememberMe)
                val response = RetrofitClient.apiService.login(loginRequest)

                //Check if accessToken and refreshToken are returned
                val accessToken = response.accessToken
                val refreshToken = response.refreshToken

                if (accessToken != null) {
                    RetrofitClient.setAccessToken(context, accessToken)
                    if (refreshToken != null) {
                        RetrofitClient.setRefreshToken(context, refreshToken)
                    }
                    _loggedIn.value = true

                    val role = JWTUtils.getClaim(accessToken, "role")
                    RetrofitClient.setRole(context, role)
                    val username = JWTUtils.getClaim(accessToken, "username")
                    RetrofitClient.setUsername(context, username)

                    println("Username: " + RetrofitClient.getUsername(context))
                    Log.i("Tokens", "AccessToken: $accessToken --- RefreshToken $refreshToken")
                    onResult("Success")
                } else {
                    onResult("Invalid credentials")
                }
            } catch (e: Exception) {
                println("Login failed: ${e.message}")
                onResult("Error")
            }
        }
    }

    fun register(email: String, username: String, password: String) {
        viewModelScope.launch {
            try {
                //Create the login request body
                val loginRequest = UserRegisterRequest(email, username, password)
                //Make the API call to the backend
                val response = RetrofitClient.apiService.register(loginRequest)
                println("Response from server${response.message}")
            } catch (e: Exception) {
                //Handle any errors
                println("Login failed: ${e.message}")
            }
        }
    }

    fun createMessage(context: Context, message: MessageDTO) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.createMessage(message)
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                //After creating the message, reload the message list
                viewMessages()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                println("Failed to fetch messages: ${e.message}")
            }
        }
    }

    fun viewMessages() {
        viewModelScope.launch {
            try {
                //Calling the server for messages
                val response = RetrofitClient.apiService.viewMessages()
                //Setting the StateFlow so we can send the data to the activity
                _messages.value = response
            } catch (e: Exception) {
                println("Failed to fetch messages: ${e.message}")
            }
        }
    }

    fun viewTasks() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.viewTasks()
                _tasks.value = response //Update the task list
                println("Tasks Retrieved: ${_tasks.value}")
            } catch (e: Exception) {
                println("Failed to fetch tasks: ${e.message}")
            }
        }
    }

    fun createTask(context: Context, task: TaskDTO) {
        viewModelScope.launch {
            try {
                //Create the task through the API
                val response = RetrofitClient.apiService.createTask(task)
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                //After creating the task, reload the task list
                viewTasks()
            } catch (e: Exception) {
                println("Failed to create task: ${e.message}")
            }
        }
    }

    fun changeTaskStatus(context: Context, task: TaskDTO) {
        viewModelScope.launch {
            try {
                //Create the task through the API
                val response = RetrofitClient.apiService.changeTaskStatus(task)
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                //After creating the task, reload the task list
                println("Changed task: $task")
                viewTasks()
            } catch (e: Exception) {
                println("Changed task: $task")
                println("Failed to change task: ${e.message}")
            }
        }
    }

    fun viewStarterTasks() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.viewStarterTasks()
                _tasks.value = response
                println("Starter Tasks Retrieved${_tasks.value}")
            } catch (e: Exception) {
                println("Failed to fetch tasks: ${e.message}")
            }
        }
    }

    suspend fun getAllUsers(){
        try {
            val response = RetrofitClient.apiService.getAllUsers()
            _users.value = response
            println("Users retrieved: ${_users.value}")
        }catch (e: Exception) {
            println("Failed ot fetch users: ${e.message}")
        }
    }

    fun logout(context: Context) {
        RetrofitClient.clearRefreshToken(context)
        RetrofitClient.clearAccessToken(context)
        RetrofitClient.clearRole(context)
        _loggedIn.value = false
    }
}
