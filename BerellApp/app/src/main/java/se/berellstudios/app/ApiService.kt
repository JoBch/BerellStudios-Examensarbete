package se.berellstudios.app

import okhttp3.internal.concurrent.Task
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//Doing it this way because its easier to reach them from other classes
data class UserLoginRequest(val email: String, val password: String)
data class UserRegisterRequest(val email: String, val username: String, val password: String)
data class MessageRequest(val message: String)
data class PingResponse(val message: String)

data class LoginResponse(val token: String)
data class MessageResponse(val message: String)

//Building a DTO so we can extract or send the data we need from it.
data class TaskDTO(
    val id: Int?,
    val messageContent: String,
    val status: String,
    val deadline: String?,
    val createdTime: String,
    val priority: Int?,
    val user_id: Int?
)

data class MessageDTO(
    val id: Int?,
    val message: String,
    val createdBy: Int?,
    val createdTime: String,
    val deadline: String?
)

interface ApiService {


    @POST("/users/login")
    suspend fun login(@Body loginRequest: UserLoginRequest): LoginResponse

    @POST("/users/register")
    suspend fun register(@Body registerRequest: UserRegisterRequest): MessageResponse

    @POST("/messages/create")
    suspend fun createMessage(
        @Body request: MessageDTO,
    ): MessageResponse

    @GET("/messages/view")
    suspend fun viewMessages(): List<String>

    @POST("/tasks/create")
    suspend fun createTask(
        @Body request: TaskDTO,
    ): MessageResponse

    @GET("/tasks/view/startertasks")
    suspend fun viewStarterTasks(): List<TaskDTO>

    @GET("/tasks/view")
    suspend fun viewTasks(): List<TaskDTO>

    @GET("/users/ping")
    fun ping(): Call<PingResponse>

}

