package com.example.ecotracker.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.example.ecotracker.ui.login.LoginScreen
import com.example.ecotracker.ui.habits.screen.AddHabitScreen
import com.example.ecotracker.ui.habits.screen.HabitsScreen
import com.example.ecotracker.ui.habits.viewmodel.HabitViewModel
import com.example.ecotracker.ui.habits.viewmodel.HabitViewModelFactory

//import com.example.ecotracker.ui.home.HomeScreen
//import com.example.ecotracker.ui.impact.ImpactScreen
//import com.example.ecotracker.ui.profile.ProfileScreen

@Composable
fun EcoTrackerNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.HABITS,
    modifier: Modifier = Modifier
) {

    val habitViewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory
    )

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 🔐 Login
        composable(Routes.LOGIN) {
//            LoginScreen(
//                onLoginSuccess = {
//                    navController.navigate(Routes.HOME) {
//                        popUpTo(Routes.LOGIN) { inclusive = true }
//                    }
//                }
//            )
        }

        // 🏠 Home (árvore + frase motivacional)
        composable(Routes.HOME) {
//            HomeScreen(
//                onNavigateToHabits = {
//                    navController.navigate(Routes.HABITS)
//                },
//                onNavigateToImpact = {
//                    navController.navigate(Routes.IMPACT)
//                },
//                onNavigateToProfile = {
//                    navController.navigate(Routes.PROFILE)
//                }
//            )
        }

        // 🌱 Lista de hábitos
        composable(Routes.HABITS) {
            HabitsScreen(
                viewModel = habitViewModel,
                onNavigateToCreateHabit = {
                    navController.navigate(Routes.ADD_HABIT)
                }
            )
        }

        // ➕ Criar hábito
        composable(Routes.ADD_HABIT) {
            AddHabitScreen(
                viewModel = habitViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 🌍 Impacto social (GPS)
        composable(Routes.IMPACT) {
//            ImpactScreen(
//                onBack = {
//                    navController.popBackStack()
//                }
//            )
        }

        // 👤 Perfil / Configurações
        composable(Routes.PROFILE) {
//            ProfileScreen(
//                onBack = {
//                    navController.popBackStack()
//                }
//            )
        }
    }
}
