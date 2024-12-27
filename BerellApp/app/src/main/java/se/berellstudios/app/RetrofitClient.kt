package se.berellstudios.app

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL_JOEL = "http://192.168.1.102:8080"  //Joels ip-adress
    private const val BASE_URL_ANDREAS = "http://DIN_DATORS_IP_HÄR:8080"  //Andreas ip-adress

    //TODO försöka göra oss av med denna och köra authInterceptor från sharedpref på nåt sätt.
    private var jwtToken: String? = null //Need to set token locally so we can use the authInterceptor

    //The authInterceptor appends the JWT token to every requests header so we dont need to do it manually
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        jwtToken?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(requestBuilder.build())
    }


    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_JOEL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    //Load the token from SharedPreferences when the app starts to set it locally for authInterceptor
    fun loadToken(context: Context) {
        jwtToken = getToken(context)
    }

    fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }

    fun setToken(context: Context, token: String) {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("token", token).apply()
        jwtToken = token //Need to set token locally so we can use the authInterceptor
    }

    fun clearToken(context: Context) {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove("token").apply()
        jwtToken = null //Need to set token locally so we can use the authInterceptor
    }

    fun setRole(context: Context, role: String?) {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("user_role", role).apply()
    }

    fun getRole(context: Context): String? {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("user_role", null)
    }

    fun clearRole(context: Context) {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove("user_role").apply()
    }

}
