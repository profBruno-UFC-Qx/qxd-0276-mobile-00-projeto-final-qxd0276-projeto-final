package com.example.ecotracker.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecotracker.ui.login.screen.LoginScreen
import com.example.ecotracker.ui.register.screen.RegisterScreen

@Composable
fun EcoTrackerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {

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
                    navController.navigate(Routes.MAIN)
                },
                onGoToLogin ={
                    navController.navigate(Routes.LOGIN)
                }
            )
        }
        // Área principal
        composable(Routes.MAIN) {
            MainScaffold()
        }
    }
}
