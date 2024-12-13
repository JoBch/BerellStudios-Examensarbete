package se.berellstudios.app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//Data classes for request and response bodies
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)
data class PingResponse(val message: String)

interface ApiService {

    //Ping endpoint to check server status, used to check the connection
    @GET("/users/ping")
    fun ping(): Call<PingResponse>

    //This is just here to remember we have this path in the server
    @POST("/users/login")
    fun loginUser(@Body credentials: LoginRequest): Call<LoginResponse>

    //This is just here to remember we have this path in the server
    @POST("/users/register")
    fun registerUser(@Body newUser: Map<String, String>): Call<Map<String, String>>
}
