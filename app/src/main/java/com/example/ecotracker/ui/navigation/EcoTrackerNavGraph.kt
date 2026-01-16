package com.example.ecotracker.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecotracker.data.local.dao.UserDao
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.ui.login.screen.LoginScreen
import com.example.ecotracker.ui.register.screen.RegisterScreen
import com.example.ecotracker.ui.start.screen.StartScreen
import com.example.ecotracker.ui.theme.ThemeViewModel

@Composable
fun EcoTrackerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.START,
        modifier = modifier
    ) {

        // Start
        composable("start") {
            StartScreen(
                onGoToHome = {
                    navController.navigate("main") {
                        popUpTo("start") { inclusive = true }
                    }
                },
                onGoToLogin = {
                    navController.navigate("login") {
                        popUpTo("start") { inclusive = true }
                    }
                }
            )
        }
        // Login
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterNewUser = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }
        // Registro
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN){
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onGoToLogin ={
                    navController.navigate(Routes.LOGIN){
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        // Área principal
        composable(Routes.MAIN) {
            MainScaffold(
                rootNavController = navController,
                themeViewModel = themeViewModel
            )
        }
    }
}
