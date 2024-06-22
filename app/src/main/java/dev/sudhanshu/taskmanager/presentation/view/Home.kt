package dev.sudhanshu.taskmanager.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import dagger.hilt.android.AndroidEntryPoint
import dev.sudhanshu.taskmanager.presentation.view.navigation.BottomNavigationBar
import dev.sudhanshu.taskmanager.presentation.view.navigation.NavigationHost
import dev.sudhanshu.taskmanager.presentation.ui.theme.TaskManagerTheme
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskManagerViewModel
import dev.sudhanshu.taskmanager.presentation.viewmodel.UserViewModel
import dev.sudhanshu.taskmanager.worker.TaskWorker
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class Home : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskManagerTheme {
                TaskManagerApp()
            }
        }
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            TaskWorker::class.java,
            1,
            TimeUnit.HOURS
        )
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)
    }
}

@Preview
@Composable
fun HomePreview() {
    TaskManagerTheme {
        TaskManagerApp()
    }
}

@Composable
fun TaskManagerApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationHost(navController = navController)
        }
    }
}



