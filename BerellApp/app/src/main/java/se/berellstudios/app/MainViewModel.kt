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

class MainViewModel : ViewModel() {

    //LiveData eller StateFlow, VEM VET
    private val _loggedIn = MutableLiveData(false)
    val loggedIn: LiveData<Boolean> get() = _loggedIn

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    private val _tasks = MutableStateFlow<List<TaskDTO>>(emptyList())
    val tasks: StateFlow<List<TaskDTO>> = _tasks

    private val _errorMessage = MutableStateFlow<String>("")
    val errorMessage: StateFlow<String> get() = _errorMessage

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
                    if(refreshToken != null){
                        RetrofitClient.setRefreshToken(context, refreshToken)
                    }
                    _loggedIn.value = true

                    val role = JWTUtils.getClaim(accessToken, "role")
                    RetrofitClient.setRole(context, role)

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

    fun createTask(context: Context, task: TaskDTO) {
        viewModelScope.launch {
            try {
                println("Deadline: " + task)
                val response = RetrofitClient.apiService.createTask(task)
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                println("Failed to create task: ${e.message}")
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

    fun viewTasks() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.viewTasks()
                _tasks.value = response
                println("Tasks Retrieved${_tasks.value}")
            } catch (e: Exception) {
                println("Failed to fetch tasks: ${e.message}")
            }
        }
    }

    fun logout(context: Context) {
        RetrofitClient.clearAccessToken(context)
        RetrofitClient.clearRole(context)
        _loggedIn.value = false
    }
}
