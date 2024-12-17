import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class UserLoginRequest(val email: String, val password: String)
data class UserRegisterRequest(val email: String, val username: String, val password: String)
data class MessageRequest(val message: String)
data class PingResponse(val message: String)

data class LoginResponse(val token: String)
data class MessageResponse(val message: String)

interface ApiService {

    //Förbereder för det vi kan behöva
    @POST("/users/login")
    suspend fun login(@Body loginRequest: UserLoginRequest): LoginResponse

    //Förbereder för det vi kan behöva
    @POST("/users/register")
    suspend fun register(@Body registerRequest: UserRegisterRequest): MessageResponse

    //Förbereder för det vi kan behöva
    @POST("/messages/create")
    suspend fun createMessage(
        @Body request: MessageRequest,
    ): MessageResponse

    //Förbereder för det vi kan behöva
    @GET("/messages/view")
    suspend fun viewMessages() : List<String>

    @GET("/users/ping")
    fun ping() : Call<PingResponse>

}

