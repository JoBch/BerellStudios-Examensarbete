package se.berellstudios.app

import android.content.Context
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

    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch {
            try {
                //Create the login request body
                val loginRequest = UserLoginRequest(email, password)
                //Make the API call to the backend
                val response = RetrofitClient.apiService.login(loginRequest)
                //Check if the response contains a token
                if (response.token != null) {
                    val token = response.token
                    RetrofitClient.setToken(context, token)  //Set the token
                    _loggedIn.value = true
                    println("Token: $token")

                    //Decode the token to get the role and saving it in sharedpref
                    val role = JWTUtils.getClaim(token, "role")
                    RetrofitClient.setRole(context, role)
                    println("User Role: $role")
                } else {
                    //Handle error if token is not present
                    println("Login failed: $response")
                }
            } catch (e: Exception) {
                //Handle any errors
                println("Login failed: ${e.message}")
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

    fun createMessage(context: Context, message: String) {
        viewModelScope.launch {
            try {
                val messageRequest = MessageRequest(message)
                val response = RetrofitClient.apiService.createMessage(messageRequest)
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                println("Failed to create message: ${e.message}")
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
        RetrofitClient.clearToken(context)
        RetrofitClient.clearRole(context)
        _loggedIn.value = false
    }
}
