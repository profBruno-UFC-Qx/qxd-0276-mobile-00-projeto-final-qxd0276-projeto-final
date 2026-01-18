package com.example.ecotracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.ecotracker.ui.habits.viewmodel.HabitViewModel
import com.example.ecotracker.ui.habits.viewmodel.HabitViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ecotracker.ui.habits.screen.AddHabitScreen
import com.example.ecotracker.ui.habits.screen.HabitsScreen
import com.example.ecotracker.ui.home.screen.HomeScreen
import com.example.ecotracker.ui.impact.screen.ImpactScreen
import com.example.ecotracker.ui.profile.screen.EditProfileScreen
import com.example.ecotracker.ui.profile.screen.ProfileScreen
import com.example.ecotracker.ui.theme.viewmodel.ThemeViewModel


@Composable
fun MainNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel
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
                } ,
                onNavigateToEditHabit = { habitId ->
                    navController.navigate(Routes.editHabit(habitId))
                }
            )
        }

        // Add hábito
        composable(Routes.ADD_HABIT) {
            AddHabitScreen(
                habitViewModel = habitViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        // EDIT Hábito
        composable(
            route = Routes.EDIT_HABIT,
            arguments = listOf(
                navArgument("habitId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable

            AddHabitScreen(
                habitId = habitId,
                habitViewModel = habitViewModel,
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
                themeViewModel = themeViewModel,
                onLogout = onLogout,
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

