package com.marcos.myspentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.marcos.myspentapp.Routes.LOGIN
import com.marcos.myspentapp.Routes.MAIN
import com.marcos.myspentapp.ui.theme.MySpentAppTheme
import androidx.compose.foundation.isSystemInDarkTheme
import com.marcos.myspentapp.ui.viewmodel.CardViewModel
import com.marcos.myspentapp.ui.viewmodel.CashViewModel
import com.marcos.myspentapp.ui.viewmodel.UserViewModel

val userViewModel = UserViewModel()
val cardViewModel = CardViewModel()
val cashViewModel = CashViewModel()


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val systemDark = isSystemInDarkTheme()
            LaunchedEffect(Unit) {
                userViewModel.setDarkTheme(systemDark)
            }

            val isDarkMode = userViewModel.userState.darkTheme

            MySpentAppTheme(isDarkMode = isDarkMode) {
                AppNavigation(
                    userViewModel = userViewModel,
                    cardViewModel = cardViewModel,
                    cashViewModel = cashViewModel
                )
            }
        }
    }
}

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val REGISTER2 = "register2"
    const val MAIN = "main"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
}

@Composable
fun AppNavigation(
    userViewModel: UserViewModel,
    cardViewModel: CardViewModel,
    cashViewModel: CashViewModel,
)
 {

    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(

        bottomBar = {
                if (showBottomBar) {
                BottomBar(
                    selectedItem = selectedIndex,
                    onItemSelected = { index ->
                        selectedIndex = index
                        when (index) {
                            0 -> navController.navigate(MAIN)
                            1 -> navController.navigate(Routes.PROFILE)
                        }
                    }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = LOGIN,
            modifier = Modifier.padding(padding)
        ) {

            composable(LOGIN) {
                showBottomBar = false
                LoginScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
            composable(Routes.REGISTER) {
                showBottomBar = false
                RegisterScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }

            composable(Routes.REGISTER2) {
                showBottomBar = false
                RegisterScreenPart2(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
            composable(MAIN) {
                showBottomBar = true
                MySpentApp(
                    cashViewModel = cashViewModel,
                    cardViewModel = cardViewModel,
                    userViewModel = userViewModel
                )
            }

            composable(Routes.PROFILE) {
                showBottomBar = true
                PerfilScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }

            composable(Routes.EDIT_PROFILE) {
                showBottomBar = false
                PerfilEdit(
                    navController = navController,
                    userViewModel = userViewModel,
                )
            }
        }
    }
}
