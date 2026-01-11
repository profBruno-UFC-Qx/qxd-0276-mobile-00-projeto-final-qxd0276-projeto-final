package com.example.ecotracker.ui.navigation

import ProfileScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.ecotracker.ui.habits.viewmodel.HabitViewModel
import com.example.ecotracker.ui.habits.viewmodel.HabitViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ecotracker.ui.habits.screen.AddHabitScreen
import com.example.ecotracker.ui.habits.screen.HabitsScreen
import com.example.ecotracker.ui.home.screen.HomeScreen
import com.example.ecotracker.ui.impact.screen.ImpactScreen
import com.example.ecotracker.ui.profile.screen.EditProfileScreen


@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val habitViewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory
    )

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {

        // Home
        composable(Routes.HOME) {
            HomeScreen()
        }

        // Hábitos
        composable(Routes.HABITS) {
            HabitsScreen(
                viewModel = habitViewModel,
                onNavigateToCreateHabit = {
                    navController.navigate(Routes.ADD_HABIT)
                }
            )
        }

        // Add hábito
        composable(Routes.ADD_HABIT) {
            AddHabitScreen(
                viewModel = habitViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Impacto
        composable(Routes.IMPACT) {
            ImpactScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Perfil
        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN){
                        popUpTo(0)
                    }
                },
                onNavigateToEditProfile = {
                    navController.navigate(Routes.EDIT_PROFILE)
                }
            )
        }
        // Editar Perfil
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}

