package com.pegai.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun FloatingBottomBar(navController: NavController) {

    val leftItems = listOf(
        BottomNavItem("Início", "home", Icons.Default.Home),
        BottomNavItem("Aluguéis", "orders", Icons.Default.DateRange),
    )

    val rightItems = listOf(
        BottomNavItem("Chat", "chat", Icons.Default.Email),
        BottomNavItem("Perfil", "profile", Icons.Default.Person),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val totalHeight = 80.dp + navBarHeight
    val mainColor = Color(0xFF0E8FC6)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalHeight),
        contentAlignment = Alignment.TopCenter
    ) {
        // Background Surface
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            shadowElevation = 16.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        leftItems.forEach { item ->
                            CustomNavItem(
                                item = item,
                                currentRoute = currentRoute,
                                navController = navController,
                                activeColor = mainColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(60.dp))

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rightItems.forEach { item ->
                            CustomNavItem(
                                item = item,
                                currentRoute = currentRoute,
                                navController = navController,
                                activeColor = mainColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(navBarHeight))
            }
        }

        // Floating Action Button
        Surface(
            modifier = Modifier
                .size(64.dp)
                .offset(y = 5.dp),
            shadowElevation = 2.dp,
            shape = CircleShape,
            color = mainColor,
            onClick = {
                navController.navigate("add") {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Anunciar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun CustomNavItem(
    item: BottomNavItem,
    currentRoute: String?,
    navController: NavController,
    activeColor: Color
) {
    val isSelected = currentRoute == item.route
    val selectedColor = activeColor
    val unselectedColor = Color.LightGray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (currentRoute != item.route) {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) selectedColor else unselectedColor
        )
    }
}