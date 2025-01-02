package se.berellstudios.app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//Doing it this way because its easier to reach them from other classes
data class UserLoginRequest(val email: String, val password: String, val rememberMe: Boolean)
data class UserRegisterRequest(val email: String, val username: String, val password: String)
data class PingResponse(val message: String)

data class LoginResponse(val accessToken: String, val refreshToken: String?)
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

data class UserDTO(
    val id: Int,
    val username: String,
    val email: String,
    val role: String
)

interface ApiService {


    @POST("/users/login")
    suspend fun login(@Body loginRequest: UserLoginRequest): LoginResponse

    @POST("/jwt/refresh")
    fun refreshToken(@Body refreshToken: Map<String, String>): Call<LoginResponse>

    @POST("/users/register")
    suspend fun register(@Body registerRequest: UserRegisterRequest): MessageResponse

    @GET("/users/getall")
    suspend fun getAllUsers(): List<UserDTO>

    @POST("/messages/delete")
    suspend fun deleteMessage(
        @Body request: MessageDTO,
    ): MessageResponse

    @POST("/messages/create")
    suspend fun createMessage(
        @Body request: MessageDTO,
    ): MessageResponse

    @GET("/messages/view")
    suspend fun viewMessages(): List<MessageDTO>

    @POST("/tasks/create")
    suspend fun createTask(
        @Body request: TaskDTO,
    ): MessageResponse

    @GET("/tasks/view/startertasks")
    suspend fun viewStarterTasks(): List<TaskDTO>

    @GET("/tasks/view")
    suspend fun viewTasks(): List<TaskDTO>

    @POST("/tasks/changestatus")
    suspend fun changeTaskStatus(
        @Body request: TaskDTO,
    ): MessageResponse

    @GET("/users/ping")
    fun ping(): Call<PingResponse>

}

