package dev.sudhanshu.taskmanager.presentation.view.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    data object TaskManager : BottomNavItem("TaskManager", Icons.Default.Home, "task_manager")
    data object Dashboard : BottomNavItem("Dashboard", Icons.Default.Menu, "dashboard")
    data object Settings : BottomNavItem("Settings", Icons.Default.Settings, "settings")
}