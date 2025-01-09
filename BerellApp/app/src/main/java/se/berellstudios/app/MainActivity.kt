package se.berellstudios.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import se.berellstudios.app.navigation.AppNavigation
import se.berellstudios.app.ui.theme.BerellAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initializeRetrofit(applicationContext)
        RetrofitClient.loadToken(applicationContext)
        val token = RetrofitClient.getAccessToken(applicationContext)
        if (token != null) {
            //Token exists, navigate to start page
            setContent {
                BerellAppTheme {
                    AppNavigation(isLoggedIn = true)
                }
            }
        } else {
            //Token doesnt exist, navigate to login
            setContent {
                BerellAppTheme {
                    AppNavigation(isLoggedIn = false)
                }
            }
        }
    }
}