package se.berellstudios.app

import MessageRequest
import UserLoginRequest
import UserRegisterRequest
import android.content.Context
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
    val messages: StateFlow<List<String>> get() = _messages

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
                    val token = response.token!!
                    RetrofitClient.setToken(token)  //Set the token
                    _loggedIn.value = true
                    println("Token: $token")
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

    fun createMessage(message: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.createMessage(
                    MessageRequest(message),
                )
                println("Time capsule created successfully!")
            } catch (e: Exception) {
                println("Failed to create time capsule: ${e.message}")
            }
        }
    }

    fun viewMessages() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.viewMessages()
                _messages.value = response
            } catch (e: Exception) {
                println("Failed to fetch time capsules: ${e.message}")
            }
        }
    }

    fun logout() {
        RetrofitClient.clearToken()
        _loggedIn.value = false
    }
}
