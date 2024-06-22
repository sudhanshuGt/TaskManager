package dev.sudhanshu.taskmanager.presentation.view.navigation


import dev.sudhanshu.taskmanager.presentation.view.component.TaskManagerScreen
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import dev.sudhanshu.taskmanager.presentation.view.component.DashboardScreen
import dev.sudhanshu.taskmanager.presentation.view.component.SettingsScreen

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.TaskManager,
        BottomNavItem.Dashboard,
        BottomNavItem.Settings
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground
    ) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.TaskManager.route) {
        composable(BottomNavItem.TaskManager.route) { TaskManagerScreen() }
        composable(BottomNavItem.Dashboard.route) { DashboardScreen() }
        composable(BottomNavItem.Settings.route) { SettingsScreen() }
    }
}

