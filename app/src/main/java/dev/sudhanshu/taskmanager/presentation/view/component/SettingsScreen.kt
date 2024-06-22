package dev.sudhanshu.taskmanager.presentation.view.component

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.presentation.ui.theme.TaskManagerTheme
import dev.sudhanshu.taskmanager.presentation.ui.theme.ThemeManager
import dev.sudhanshu.taskmanager.presentation.view.Login
import dev.sudhanshu.taskmanager.presentation.viewmodel.UserViewModel
import dev.sudhanshu.taskmanager.util.Resource
import dev.sudhanshu.taskmanager.util.SettingsPreferences

@Composable
fun SettingsScreen(userViewModel: UserViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val settingsPreferences = remember { SettingsPreferences.getInstance(context) }

    var darkModeEnabled by remember { mutableStateOf(settingsPreferences.isDarkModeEnabled()) }
    var notificationsEnabled by remember { mutableStateOf(settingsPreferences.areNotificationsEnabled()) }

    // Request notification permission launcher
    val requestNotificationPermission =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, save the notification preference
                settingsPreferences.setNotificationsEnabled(true)
                notificationsEnabled = true
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(context, "For location-based reminders, notification permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    LaunchedEffect(key1 = true) {
        userViewModel.signOut.collect { result ->
            when (result) {
                is Resource.Success -> {
                    val intent = Intent(context, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
                is Resource.Error -> {
                    // Handle error
                }
                is Resource.Loading -> {
                    // Handle loading state
                }
            }
        }
    }

    TaskManagerTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Settings") },
                    backgroundColor = MaterialTheme.colors.background,
                    modifier = Modifier.background(MaterialTheme.colors.background)
                )
            },
            content = { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                        .padding(padding)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 100.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                            Text(
                                text = if (darkModeEnabled) "Switch to light mode" else "Switch to Dark Mode",
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = darkModeEnabled,
                                onCheckedChange = {
                                    darkModeEnabled = it
                                    settingsPreferences.setDarkModeEnabled(it)
                                    ThemeManager.isDarkModeEnabled = it
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                            Text(
                                text = if (notificationsEnabled) "Disable notifications" else "Enable notifications",
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = {
                                    notificationsEnabled = it
                                    if (it) {
                                        // Check and request notification permission if not granted
                                        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                                            requestNotificationPermission.launch(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                                        } else {
                                            settingsPreferences.setNotificationsEnabled(it)
                                        }
                                    } else {
                                        settingsPreferences.setNotificationsEnabled(it)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .clickable {
                                    userViewModel.signOutUser()
                                }
                        ) {
                            Text(text = "Sign out", modifier = Modifier.weight(1f))
                            Image(
                                painter = painterResource(id = R.drawable.log_out),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SettingsScreenWrapper() {
    TaskManagerTheme {
        SettingsScreen()
    }
}
