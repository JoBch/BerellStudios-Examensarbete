package se.berellstudios.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import se.berellstudios.app.MainViewModel
import se.berellstudios.app.screens.*

@Composable
fun AppNavigation(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()

    val startDestination = if (isLoggedIn) "landing" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LogInScreen(navController, mainViewModel) }
        composable("landing") { LandingScreen(navController, mainViewModel) }
        composable("messages") { MessagesScreen(navController, mainViewModel) }
        composable("tasks") { TasksScreen(navController, mainViewModel) }
        composable("createuser") { CreateUserScreen(navController, mainViewModel) }
    }
}
