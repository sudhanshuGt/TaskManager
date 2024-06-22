package dev.sudhanshu.taskmanager.presentation.view.component

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.Timestamp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskViewModel
import dev.sudhanshu.taskmanager.presentation.ui.theme.TaskManagerTheme
import dev.sudhanshu.taskmanager.presentation.ui.theme.Typography
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskManagerViewModel
import dev.sudhanshu.taskmanager.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TaskDetailScreen(
    taskId: String,
    taskManagerViewModel: TaskManagerViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    onTaskDelete: () -> Unit,
    onBack: () -> Unit,
    onEditTask: (Task) -> Unit
) {
    TaskManagerTheme {


        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val scaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()
        var refreshTrigger by remember { mutableStateOf(false) }

        fun fetchDetail() {
            taskViewModel.getTaskById(
                taskId = taskId,
                onSuccess = {},
                onFailure = { error ->
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "Error: ${error.message}",
                            actionLabel = "Dismiss"
                        )
                    }
                }
            )
        }

        LaunchedEffect(key1 = taskId) {
            fetchDetail()
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    fetchDetail()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        val task by remember(taskViewModel.task) { taskViewModel.task }.collectAsState(initial = null)

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text(text = "Task Details", color = MaterialTheme.colors.onBackground) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colors.onBackground)
                        }
                    },
                    backgroundColor = MaterialTheme.colors.background
                )
            },
            content = { padding ->
                task?.let {
                    padding.calculateTopPadding()
                    TaskDetailContent(task!!, taskViewModel, taskManagerViewModel, onTaskDelete, onEditTask, onTaskUpdate = { refreshTrigger = it })
                }
            }
        )
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    taskViewModel: TaskViewModel,
    taskManagerViewModel: TaskManagerViewModel,
    onTaskDelete: () -> Unit,
    onEditTask: (Task) -> Unit,
    onTaskUpdate: (Boolean) -> Unit
) {
    var isCompleted by remember { mutableStateOf(task.isCompleted) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colors.background)
    ) {
        Text(
            text = task.title,
            style = Typography.h1,
            fontSize = 24.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = task.description ?: "No description provided",
            style = Typography.h4,
            fontSize = 16.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Due Date: ${formatDueDate(task.dueDate)}",
            style = Typography.h3,
            fontSize = 16.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Priority: ${task.priority}",
            style = Typography.h3.copy(color = priorityColor(task.priority)),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (task.location != null) {
            val cameraPositionState = rememberCameraPositionState {
                position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                    com.google.android.gms.maps.model.LatLng(task.location!!.latitude, task.location!!.longitude), 15f
                )
            }

            val markerState = rememberMarkerState(
                position = com.google.android.gms.maps.model.LatLng(task.location!!.latitude, task.location!!.longitude)
            )

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                onMapClick = {
                    onEditTask(task)
                },
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = markerState,
                    title = task.title,
                    draggable = false
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {


            Switch(
                checked = isCompleted,
                onCheckedChange = { isChecked ->
                    if (!task.isCompleted) {
                        isCompleted = isChecked
                        task.isCompleted = isChecked
                        taskViewModel.updateTask(task, onSuccess = {
                            onTaskUpdate(isChecked)
                        }, onFailure = {
                            Toast.makeText(context, "Error updating task", Toast.LENGTH_SHORT).show()
                        })
                    }
                }
            )

            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (task.isCompleted) "Task is completed" else "Mark as completed",
                color = Color.Gray,
                fontSize = 14.sp,
                style = Typography.h3
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!task.isCompleted) {
                Button(
                    onClick = {
                        onEditTask(task)
                    },
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colors.background)
                ) {
                    Text(
                        text = "Edit Task",
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 14.sp,
                        style = Typography.h3
                    )
                }
            }

            Button(
                onClick = {
                    taskManagerViewModel.deleteUserTask(task.id, onSuccess = {
                        onTaskDelete()
                    }, onError = {})
                },
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 20.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colors.background)
            ) {
                Text(
                    text = "Delete Task",
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 14.sp,
                    style = Typography.h3
                )
            }
        }
    }
}

@SuppressLint("NewApi")
private fun formatDueDate(dueDate: Timestamp?): String {
    dueDate ?: return "Not specified"

    val instant = dueDate.toDate().toInstant()
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, hh:mm a")

    return dateTime.format(formatter)
}

@Composable
fun priorityColor(priority: String): Color {
    return when (priority.toLowerCase(Locale.ROOT)) {
        "high" -> Color.Red
        "medium" -> Color.Green
        "low" -> Color.Gray
        else -> Color.LightGray
    }
}
