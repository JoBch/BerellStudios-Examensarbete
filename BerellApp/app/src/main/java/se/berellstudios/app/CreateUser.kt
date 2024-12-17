package se.berellstudios.app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.berellstudios.app.PingResponse
import se.berellstudios.app.RetrofitClient
import se.berellstudios.app.ui.theme.BerellAppTheme

class CreateUser : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val PREFS_NAME = "MyAppPrefs"
        val TOKEN_KEY = "jwt_token"
        setContent {
            BerellAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {

                    }
                }
            }
        }
    }

    private fun callPingApi() {
        RetrofitClient.apiService.ping().enqueue(object : Callback<PingResponse> {
            override fun onResponse(call: Call<PingResponse>, response: Response<PingResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "No message"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Server Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PingResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.d("Berell", "\"Network Error: ${t.message}\"")
            }
        })
    }
}



/*function for saving token to sharedPrefs
private fun saveToken(token: String) {
    val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
}*/