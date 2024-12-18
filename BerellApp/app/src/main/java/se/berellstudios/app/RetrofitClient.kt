package se.berellstudios.app

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL_JOEL = "http://192.168.1.102:8080"  //Joels ip-adress
    private const val BASE_URL_ANDREAS = "http://DIN_DATORS_IP_HÄR:8080"  //Andreas ip-adress

    /***
     * Du kommer behöva ändra ip-adressen i res/xml/network_security_config.xml
     * till samma som din ip-adress här uppe. Har lagt till i .gitignore så vi borde bara behöva göra detta en gång.
     * Det som händer i den är att vi tvingar appen att köra utan SSL(https). Detta är nåt vi kommer
     * få ändra i framtiden för att det ska vara rätt och fint men det löser vi när servern ligger på raspberry tänker jag.
     *
     * Du kommer nog även behöva öppna port 8080 i din brandvägg(ska också bara göras en gång):
     * Open Windows Defender Firewall.
     * Click on Advanced settings.
     * Create a new inbound rule that allows TCP traffic on the port your Spring Boot server is listening to (usually 8080).
     * Go to Inbound Rules -> New Rule.
     * Select Port, and choose TCP.
     * Specify the port, such as 8080 (or whatever port your Spring Boot app is running on(det är 8080 om du inte har nåt annat där)).
     * Allow the connection, then give it a name (like Spring Boot Server Port).
     *
     * Tror inte vi ska pilla mer i denna sen så då kan vi kanske lägga till denna i .gitignore med.
     * ***/

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

    //Tror detta borde funka för att behålla allt. INTE glömma att kalla på allt där det ska kallas på bara

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
        jwtToken = null
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
