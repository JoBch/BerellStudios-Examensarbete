package se.berellstudios.app

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL_JOEL = "http://192.168.1.145:8080"  //Joels IP address
    private const val BASE_URL_ANDREAS =
        "http://192.168.1.139:8080"  //Andreas IP address TODO visst slutade din på .139?
    private var jwtToken: String? = null //Used locally for authInterceptor
    lateinit var apiService: ApiService

    //Intercept outgoing requests and handle token expiration
    private fun createAuthInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            //Retrieve the current token from SharedPreferences or other storage
            val token = getAccessToken(context)

            //Create a new request with the Authorization header if the token is available
            var requestBuilder = chain.request().newBuilder()

            //If a token exists, add the Authorization header
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            //Proceed with the request (it can now have the Authorization header)
            var response = chain.proceed(requestBuilder.build())

            //If the response is 400/401 Unauthorized, attempt to refresh the token
            if (response.code == 400 || response.code == 401) {
                //Try refreshing the token
                val refreshToken = getRefreshToken(context)
                if (refreshToken != null) {
                    val newAccessToken = refreshAccessToken(refreshToken, context)
                    //Closing the old response so we can create a new
                    response.close()

                    //If new access token is retrieved, retry the original request with the new token
                    if (newAccessToken != null) {
                        //Save the new token
                        setAccessToken(context, newAccessToken)

                        //Retry the request with the new token
                        requestBuilder = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $newAccessToken")
                        response = chain.proceed(requestBuilder.build())
                    }
                }
            }
            response
        }
    }


    //OkHttp client with the interceptor
    private fun createClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createAuthInterceptor(context))
            .build()
    }

    //Retrofit service
    fun initializeRetrofit(context: Context) {
        val client = createClient(context)
        apiService = Retrofit.Builder()
            .baseUrl(BASE_URL_JOEL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


    //Refresh the access token using the refresh token
    private fun refreshAccessToken(refreshToken: String, context: Context): String? {
        return try {
            val response = apiService.refreshToken(mapOf("refreshToken" to refreshToken)).execute()

            if (response.isSuccessful) {
                val newAccessToken = response.body()?.accessToken
                newAccessToken?.let {
                    setAccessToken(context, it) //Save the new access token
                }
                newAccessToken
            } else {
                println("Failed to refresh token: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            println("Exception during token refresh: ${e.message}")
            null
        }
    }

    //Helper function to retrieve EncryptedSharedPreferences for safer storage
    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        //Retrieve the master key
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        //Initialize EncryptedSharedPreferences
        return EncryptedSharedPreferences.create(
            masterKey,
            "my_secure_prefs",
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    // Load the token from EncryptedSharedPreferences
    fun loadToken(context: Context) {
        jwtToken = getAccessToken(context)
    }

    // Set username
    fun setUsername(context: Context, username: String?) {
        val sharedPref = getEncryptedSharedPreferences(context)
        sharedPref.edit().putString("username", username).apply()
    }

    // Get username
    fun getUsername(context: Context): String? {
        val sharedPref = getEncryptedSharedPreferences(context)
        return sharedPref.getString("username", null)
    }

    // Set role
    fun setRole(context: Context, role: String?) {
        val sharedPref = getEncryptedSharedPreferences(context)
        sharedPref.edit().putString("user_role", role).apply()
    }

    // Get role
    fun getRole(context: Context): String? {
        val sharedPref = getEncryptedSharedPreferences(context)
        return sharedPref.getString("user_role", null)
    }

    // Clear role
    fun clearRole(context: Context) {
        val sharedPref = getEncryptedSharedPreferences(context)
        sharedPref.edit().remove("user_role").apply()
    }

    // Set refresh token
    fun setRefreshToken(context: Context, refreshToken: String) {
        val sharedPref = getEncryptedSharedPreferences(context)
        sharedPref.edit().putString("refreshToken", refreshToken).apply()
    }

    // Clear refresh token
    fun clearRefreshToken(context: Context) {
        val sharedPref = getEncryptedSharedPreferences(context)
        sharedPref.edit().remove("refreshToken").apply()
    }

    // Get refresh token
    fun getRefreshToken(context: Context): String? {
        val sharedPref = getEncryptedSharedPreferences(context)
        return sharedPref.getString("refreshToken", null)
    }

    // Set access token
    fun setAccessToken(context: Context, token: String) {
        val sharedPref = getEncryptedSharedPreferences(context)
        sharedPref.edit().putString("accessToken", token).apply()
        jwtToken = token // Update local reference for the interceptor
    }

    // Get access token
    fun getAccessToken(context: Context): String? {
        val sharedPref = getEncryptedSharedPreferences(context)
        return sharedPref.getString("accessToken", null)
    }

    // Clear access token
    fun clearAccessToken(context: Context) {
        val sharedPref = getEncryptedSharedPreferences(context)
        sharedPref.edit().remove("accessToken").apply()
        jwtToken = null
    }
}

